package ru.acuma.shuffler.service.message.content;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.acuma.shuffler.context.Render;
import ru.acuma.shuffler.model.domain.TEvent;

@Service
@RequiredArgsConstructor
public class CheckingContent implements Fillable, WithText<TEvent>, WithKeyboard<TEvent> {

    @Override
    public void fill(final Render render, final Long chatId) {

    }

    @Override
    public void fillKeyboard(Render render, TEvent subject) {

    }

    @Override
    public void fillText(Render render, TEvent subject) {

    }
}
