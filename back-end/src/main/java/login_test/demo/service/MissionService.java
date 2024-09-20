package login_test.demo.service;

import login_test.demo.dto.AchievementDto;
import login_test.demo.dto.MissionDto;
import login_test.demo.model.Achievement;
import login_test.demo.model.Mission;
import login_test.demo.repository.AchievementRepository;
import login_test.demo.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;
    private final AchievementRepository achievementRepository;

    // 미션 추가
    public void addMission(MissionDto missionDto) {
        Mission mission = Mission.builder()
                .title(missionDto.getTitle())
                .content(missionDto.getContent())
                .missionReward(missionDto.getMissionReward())
                .build();

        missionRepository.save(mission);
    }


    // 업적 추가
    public void addAchievement(AchievementDto achievementDto) {
        Achievement achievement = Achievement.builder()
                .title(achievementDto.getTitle())
                .content(achievementDto.getContent())
                .achievementReward(achievementDto.getAchievementReward())
                .build();

        achievementRepository.save(achievement);
    }
}