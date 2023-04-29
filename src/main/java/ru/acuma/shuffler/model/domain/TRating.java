package ru.acuma.shuffler.model.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@Builder
@Accessors(chain = true)
public class TRating implements Serializable {

    private Boolean calibrated;
    private Integer score;
    private Integer eventScoreChange;
    private Integer lastScoreChange;
    private Integer calibrationMultiplier;

    public void applyScore(Integer change) {
        var scoreChange = change * calibrationMultiplier;
        score += scoreChange;

        eventScoreChange = getCalibrated()
                           ? eventScoreChange + scoreChange
                           : 0;
        lastScoreChange = scoreChange;
    }
}
