package ru.acuma.shuffler.service.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.acuma.shuffler.exception.DataException;
import ru.acuma.shuffler.mapper.GroupMapper;
import ru.acuma.shuffler.model.entity.GroupInfo;
import ru.acuma.shuffler.model.constant.ExceptionCause;
import ru.acuma.shuffler.repository.GroupInfoRepository;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupMapper groupMapper;
    private final GroupInfoRepository groupInfoRepository;

    public GroupInfo getGroupInfo(final Long chatId) {
        return groupInfoRepository.findById(chatId)
            .orElseThrow(() -> new DataException(ExceptionCause.GROUP_NOT_FOUND, chatId));
    }

    @Transactional
    public boolean signIn(Chat chat) {
        if (!(chat.isGroupChat() || chat.isSuperGroupChat())) {
            return false;
        }
        var groupInfo = getGroupInfo(chat.getId());
        groupMapper.mergeGroupInfo(groupInfo, chat);

        return groupInfo.getIsActive();
    }
}
