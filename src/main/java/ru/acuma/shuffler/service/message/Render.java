package ru.acuma.shuffler.service.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.acuma.shuffler.model.constant.messages.ExecuteStrategy;
import ru.acuma.shuffler.model.constant.messages.MessageAction;
import ru.acuma.shuffler.model.constant.messages.MessageAfterAction;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@Accessors(chain = true)
public class Render implements Serializable {

    private Integer messageId;
    private ExecuteStrategy executeStrategy;
    private Long delay;
    private MessageAction messageAction;
    private List<MessageAfterAction> afterActions;

    public boolean requireChanges() {
        return ExecuteStrategy.IDLE != executeStrategy;
    }

    public static Render forSend() {
        return Render.builder()
            .executeStrategy(ExecuteStrategy.REGULAR)
            .messageAction(MessageAction.SEND)
            .build();
    }

    public static Render forUpdate(final Integer messageId) {
        return Render.builder()
            .messageId(messageId)
            .executeStrategy(ExecuteStrategy.REGULAR)
            .messageAction(MessageAction.UPDATE)
            .build();
    }

    public static Render forMarkup(final Integer messageId) {
        return Render.builder()
            .messageId(messageId)
            .executeStrategy(ExecuteStrategy.REGULAR)
            .messageAction(MessageAction.UPDATE_MARKUP)
            .build();
    }

    public static Render forDelete(final Integer messageId) {
        return Render.builder()
            .messageId(messageId)
            .executeStrategy(ExecuteStrategy.REGULAR)
            .messageAction(MessageAction.DELETE)
            .build();
    }

    public Render withAfterAction(final MessageAfterAction afterAction) {
        afterActions.add(afterAction);

        return this;
    }

    public Render withDelay(final Long delay) {
        return setDelay(delay)
            .setExecuteStrategy(ExecuteStrategy.DELAYED);
    }

    public Render withSchedule() {
        return setExecuteStrategy(ExecuteStrategy.SCHEDULED);
    }

    public void success() {
        setExecuteStrategy(ExecuteStrategy.IDLE);
    }
}
