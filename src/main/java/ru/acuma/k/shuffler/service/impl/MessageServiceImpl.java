package ru.acuma.k.shuffler.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.acuma.k.shuffler.service.MessageService;

@Log4j2
@Service
public class MessageServiceImpl implements MessageService {

    @Override
    public Message income(Message message){


        return message;
    }


}
