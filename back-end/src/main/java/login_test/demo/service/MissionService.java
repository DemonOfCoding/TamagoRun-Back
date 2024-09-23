package login_test.demo.service;

import login_test.demo.dto.AchievementDto;
import login_test.demo.dto.MissionDto;
import login_test.demo.model.Achievement;
import login_test.demo.model.Mission;
import login_test.demo.model.User;
import login_test.demo.repository.AchievementRepository;
import login_test.demo.repository.MissionRepository;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final AchievementRepository achievementRepository;

    // 미션 추가
    public void addMission(MissionDto missionDto) {

        User user = userRepository.findById(missionDto.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Mission mission = Mission.builder()
                .title(missionDto.getTitle())
                .content(missionDto.getContent())
                .missionReward(missionDto.getMissionReward())
                .user(user)
                .build();

        missionRepository.save(mission);
    }


    // 업적 추가
    public void addAchievement(AchievementDto achievementDto) {

        User user = userRepository.findById(achievementDto.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Achievement achievement = Achievement.builder()
                .title(achievementDto.getTitle())
                .content(achievementDto.getContent())
                .achievementReward(achievementDto.getAchievementReward())
                .user(user)
                .build();

        achievementRepository.save(achievement);
    }
}