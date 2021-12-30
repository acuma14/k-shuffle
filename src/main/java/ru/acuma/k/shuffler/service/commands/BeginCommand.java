package ru.acuma.k.shuffler.service.commands;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.acuma.k.shuffler.cache.EventContextServiceImpl;
import ru.acuma.k.shuffler.model.enums.Command;
import ru.acuma.k.shuffler.service.EventStateService;
import ru.acuma.k.shuffler.service.ExecuteService;
import ru.acuma.k.shuffler.service.MessageService;

import static ru.acuma.k.shuffler.model.enums.messages.MessageType.CHECKING;

@Component
public class BeginCommand extends BaseBotCommand {

    private final EventContextServiceImpl eventContextService;
    private final EventStateService eventStateService;
    private final ExecuteService executeService;
    private final MessageService messageService;

    public BeginCommand(EventContextServiceImpl eventContextService, EventStateService eventStateService, ExecuteService executeService, MessageService messageService) {
        super(Command.BEGIN.getCommand(), "Начать турнир");
        this.eventContextService = eventContextService;
        this.eventStateService = eventStateService;
        this.executeService = executeService;
        this.messageService = messageService;
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, Message message) {
        final var event = eventContextService.buildEvent(message.getChatId());

        eventStateService.beginCheckState(event);
        executeService.execute(absSender, messageService.updateLobbyMessage(event));
        executeService.execute(absSender, messageService.sendMessage(event, CHECKING));
    }
}

