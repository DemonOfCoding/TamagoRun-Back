package login_test.demo.service;

import login_test.demo.dto.AchievementDto;
import login_test.demo.dto.MissionDto;
import login_test.demo.model.Achievement;
import login_test.demo.model.GameCharacter;
import login_test.demo.model.Mission;
import login_test.demo.model.User;
import login_test.demo.repository.AchievementRepository;
import login_test.demo.repository.GameCharacterRepository;
import login_test.demo.repository.MissionRepository;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final AchievementRepository achievementRepository;
    private final GameCharacterRepository gameCharacterRepository;

    // 미션 추가
    public void addMission(MissionDto missionDto) {
        User user = userRepository.findById(missionDto.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Mission mission = Mission.builder()
                .title(missionDto.getTitle())
                .content(missionDto.getContent())
                .missionReward(missionDto.getMissionReward())
                .missionType(missionDto.getMissionType()) // missionType 추가
                .user(user)
                .build();

        missionRepository.save(mission);
    }

    // 미션 완료 시 경험치 추가
    public void completeMission(Long missionId, Long characterId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("미션을 찾을 수 없습니다."));

        GameCharacter character = gameCharacterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));

        if (!mission.isMissionStatus()) { // 미션이 완료되지 않은 경우
            character.setExperience(character.getExperience() + mission.getMissionReward());
            mission.setMissionStatus(true); // 미션 상태 업데이트
            missionRepository.save(mission);
            gameCharacterRepository.save(character);
        }
    }

    // 일일 미션 리셋
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    public void resetDailyMissions() {
        missionRepository.findAll().stream()
                .filter(mission -> mission.getMissionType() == Mission.MissionType.DAILY)
                .forEach(mission -> {
                    mission.setMissionStatus(false); // 상태 리셋
                    missionRepository.save(mission);
                });
    }

    // 주간 미션 리셋
    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 자정
    public void resetWeeklyMissions() {
        missionRepository.findAll().stream()
                .filter(mission -> mission.getMissionType() == Mission.MissionType.WEEKLY)
                .forEach(mission -> {
                    mission.setMissionStatus(false); // 상태 리셋
                    missionRepository.save(mission);
                });
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