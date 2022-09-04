package ru.acuma.shuffler.service.commands;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.acuma.shuffler.cache.EventContextServiceImpl;
import ru.acuma.shuffler.model.enums.Command;
import ru.acuma.shuffler.model.enums.EventState;
import ru.acuma.shuffler.model.enums.GameState;
import ru.acuma.shuffler.model.enums.messages.MessageType;
import ru.acuma.shuffler.service.api.EventStateService;
import ru.acuma.shuffler.service.api.ExecuteService;
import ru.acuma.shuffler.service.api.MessageService;

@Component
public class CancelCommand extends BaseBotCommand {

    private final EventContextServiceImpl eventContextService;
    private final EventStateService eventStateService;
    private final ExecuteService executeService;
    private final MessageService messageService;

    public CancelCommand(EventContextServiceImpl eventContextService, EventStateService eventStateService, ExecuteService executeService, MessageService messageService) {
        super(Command.CANCEL.getCommand(), "Отменить турнир");
        this.eventContextService = eventContextService;
        this.eventStateService = eventStateService;
        this.executeService = executeService;
        this.messageService = messageService;
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, Message message) {
        final var event = eventContextService.getCurrentEvent(message.getChatId());
        var gameState = event.getLatestGame().getState();
        if (gameState.in(GameState.BLUE_CHECKING, GameState.RED_CHECKING, GameState.CANCEL_CHECKING) || event.getEventState().in(EventState.FINISH_CHECKING)) {
            return;
        }

        eventStateService.cancelCheckState(event);
        executeService.execute(absSender, messageService.updateLobbyMessage(event));
        executeService.executeAsyncTimer(absSender, event, messageService.sendMessage(event, MessageType.CHECKING_TIMED));
    }
}

