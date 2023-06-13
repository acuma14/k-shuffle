package ru.acuma.shuffler.service.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.acuma.shuffler.mapper.RatingMapper;
import ru.acuma.shuffler.model.constant.Constants;
import ru.acuma.shuffler.model.constant.Discipline;
import ru.acuma.shuffler.model.domain.TEvent;
import ru.acuma.shuffler.model.domain.TEventPlayer;
import ru.acuma.shuffler.model.domain.TGame;
import ru.acuma.shuffler.model.domain.TGameBet;
import ru.acuma.shuffler.model.domain.TRating;
import ru.acuma.shuffler.model.domain.TTeam;
import ru.acuma.shuffler.model.entity.Player;
import ru.acuma.shuffler.model.entity.Rating;
import ru.acuma.shuffler.model.entity.RatingHistory;
import ru.acuma.shuffler.repository.RatingHistoryRepository;
import ru.acuma.shuffler.repository.RatingRepository;
import ru.acuma.shuffler.service.season.SeasonService;

import javax.management.InstanceNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final SeasonService seasonService;
    private final RatingHistoryRepository ratingHistoryRepository;
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final CalibrationService calibrationService;

    @Value("${rating.calibration.game-penalty}")
    private float calibrationPenaltyMultiplier;

    public void applyBet(TTeam redTeam, TTeam blueTeam) {
        boolean isCalibrating = redTeam.containsCalibrating() || blueTeam.containsCalibrating();

        var redWinCase = winCase(redTeam, blueTeam);
        var limitedRedWinCase = limitAndRoundChange(redWinCase, isCalibrating);
        var blueWinCase = getBasePool(isCalibrating) - limitedRedWinCase;

        var redLoseCase = -1 * blueWinCase;
        var blueLoseCase = -1 * limitedRedWinCase;

        var redBet = TGameBet.builder().caseWin(limitedRedWinCase).caseLose(redLoseCase).build();
        var blueBet = TGameBet.builder().caseWin(blueWinCase).caseLose(blueLoseCase).build();

        redTeam.setBet(redBet);
        blueTeam.setBet(blueBet);
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public void update(TEvent event) {
        var game = event.getCurrentGame();
        Optional.ofNullable(game.getWinnerTeam())
            .orElseThrow(() -> new InstanceNotFoundException("Отсутствует победившая команда"));
        applyChanges(event);
    }
    public TRating getRating(final Player player, final Discipline discipline) {
        return ratingRepository.findBySeasonAndPlayerAndDiscipline(
                seasonService.getCurrentSeason(),
                player,
                discipline)
            .map(ratingMapper::toRatingContext)
            .orElseGet(ratingMapper::defaultRating);
    }

    private int winCase(final TTeam team1, final TTeam team2) {
        var diff = team1.getScore() - team2.getScore();
        var limitedDiff = Math.min(diff, Constants.RATING_REFERENCE);
        var change = diff >= 0
                     ? Constants.BASE_RATING_CHANGE * (1 - ((float) limitedDiff / Constants.RATING_REFERENCE))
                     : Constants.BASE_RATING_CHANGE * (1 + ((float) -limitedDiff / Constants.RATING_REFERENCE));

        return Math.round(change);
    }

    private int limitAndRoundChange(float change, boolean isCalibrating) {
        var value = isCalibrating
                    ? change * calibrationPenaltyMultiplier
                    : change;

        if (Math.abs(value) >= getBasePool(isCalibrating)) {
            return getBasePool(isCalibrating) - 1;
        }

        return Math.max(Math.abs(Math.round(value)), 1);
    }

    private int getBasePool(boolean isCalibrating) {
        return isCalibrating ? Constants.BASE_RATING_CHANGE : Constants.BASE_RATING_CHANGE * 2;
    }

    private Rating newDefaultRating(Player player, Discipline discipline) {
        var rating = Rating.builder()
            .discipline(discipline)
            .season(seasonService.getCurrentSeason())
            .player(player)
            .score(Constants.BASE_RATING)
            .isCalibrated(false)
            .build();

        return ratingRepository.save(rating);
    }

    private void applyCalibratingStatus(TEventPlayer player, Discipline discipline) {
        var isCalibrated = calibrationService.isCalibrated(player.getId(), discipline);
        player.getRatingContext().setCalibrated(isCalibrated);
    }

    private void applyChanges(TEvent event) {
        var game = event.getCurrentGame();
        game.getPlayers().forEach(player -> applyCalibratingStatus(player, event.getDiscipline()));
        List.of(game.getWinnerTeam(), game.getLoserTeam()).forEach(TTeam::applyRating);
    }
}
