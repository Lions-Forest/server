package com.example.lionsforest.domain.group.service;

import com.example.lionsforest.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MeetingScheduler {
    private final GroupService groupService;

    // 1분마다 실행
    @Scheduled(cron = "0 * * * * *")
    public void closeExpiredMeetings() {
        groupService.closeExpiredMeetings();
    }
}
