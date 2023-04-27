package ru.acuma.shuffler.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.acuma.shuffler.controller.BeginCommand;
import ru.acuma.shuffler.model.constant.EventState;
import ru.acuma.shuffler.model.domain.TgEvent;
import ru.acuma.shuffler.service.api.EventStateService;

import java.util.List;
import java.util.function.BiConsumer;

import static ru.acuma.shuffler.model.constant.EventState.READY;

@Service
@RequiredArgsConstructor
public class BeginCommandHandler extends BaseCommandHandler<BeginCommand> {

    private final EventStateService eventStateService;

    @Override
    protected List<EventState> getSupportedStates() {
        return List.of(READY);
    }

    @Override
    public void handle(Message message, String... args) {

    }

    private BiConsumer<Message, TgEvent> getReadyConsumer() {
        return (message, event) -> {
            eventStateService.begin(event);
//            var lobbyMessage = messageService.buildLobbyMessageUpdate(event);
//            var checkingMessage = messageService.buildMessage(event, MessageType.CHECKING);
//            executeService.execute(lobbyMessage);
//            executeService.execute(checkingMessage);
        };
    }
}
