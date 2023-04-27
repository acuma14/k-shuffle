package ru.acuma.shuffler.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.acuma.shuffler.model.constant.Command;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitCommand extends BaseBotCommand {

    @Override
    public void execute(Message message, String... args) {
        log.debug("Нужно подождать таймер");
    }

    @Override
    public String getCommandIdentifier() {
        return Command.WAIT.getCommand();
    }
}

