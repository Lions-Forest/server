package com.example.lionsforest.domain.notification.scheduler;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.GroupPhoto;
import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.group.repository.GroupPhotoRepository;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.group.repository.ParticipationRepository;
import com.example.lionsforest.domain.notification.Notification;
import com.example.lionsforest.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;
    private final ParticipationRepository participationRepository;
    private final GroupPhotoRepository groupPhotoRepository;

    // 1분마다 실행되는 스케줄러 (fixedRate = 60000ms)
    @Scheduled(fixedRate = 60000)
    public void notifyEventsStartingSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1).withSecond(0).withNano(0);
        // 1시간 후 (±1분 이내) 시작하는 모임 목록 조회
        LocalDateTime startRange = oneHourLater.minusMinutes(1);
        LocalDateTime endRange = oneHourLater.plusMinutes(1);
        List<Group> upcomingGroups = groupRepository.findByMeetingAtBetween(startRange, endRange);

        for (Group group : upcomingGroups) {

            List<Participation> participations = participationRepository.findByGroupId(group.getId());
            // 모임 첫 사진 가져오기
            String photoPath = null;
            Optional<GroupPhoto> firstPhotoOpt = groupPhotoRepository.findFirstByGroupIdOrderByPhotoOrderAsc(group.getId());
            if (firstPhotoOpt.isPresent()) {
                photoPath = firstPhotoOpt.get().getPhoto();
            }

            String content = String.format("⏰ '[%s] %s' 모임 1시간 전입니다!",
                    group.getMeetingAt().format(DateTimeFormatter.ofPattern("yy.MM.dd")),
                    group.getTitle());

            for (Participation part : participations) {
                // 중복 알림 생성 체크
                if (!notificationRepository.existsByUserAndContent(part.getUser(), content)) {
                    Notification notification = Notification.builder()
                            .user(part.getUser())
                            .content(content)
                            .photo(photoPath)
                            .build();
                    notificationRepository.save(notification);
                }
            }
        }
    }
}

