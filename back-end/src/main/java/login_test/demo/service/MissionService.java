package login_test.demo.service;

import login_test.demo.model.*;
import login_test.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final RunningRepository runningRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final WeeklyMissionRepository weeklyMissionRepository;
    private final GameCharacterRepository gameCharacterRepository;

    // 일일 미션 평가
    public void evaluateDailyMissions(Long userId) {
        List<DailyMission> missions = dailyMissionRepository.findByUserId(userId);
        List<GameCharacter> gameCharacters = gameCharacterRepository.findAllByUserId(userId);

        if (missions.isEmpty()) {
            return; // 미션이 없으면 종료
        }

        // 활성 캐릭터 탐색
        GameCharacter activeCharacter = findActiveCharacter(gameCharacters);

        if (activeCharacter == null) {
            return; // 레벨이 4인 캐릭터만 존재하면 종료
        }

        DailyMission dailyMission = missions.get(0); // Assume one DailyMission per user
        double dailyDistance = dailyMission.getDailyRunningDistance();
        int dailyRunningTime = dailyMission.getDailyRunningTime();

        int gainExp = 0;

        // 미션 클리어 평가
        for (DailyMission mission : missions) {
            // 3km 미션 완료 조건
            if (dailyDistance >= 3 && !mission.isMissionStatus1()) {
                mission.setMissionStatus1(true);
                gainExp += 2000;
            }

            // 5km 미션 완료 조건
            if (dailyDistance >= 5 && !mission.isMissionStatus2()) {
                mission.setMissionStatus2(true);
                gainExp += 4000;
            }

            // 30분 달리기 미션 완료 조건
            if (dailyRunningTime >= 30 && !mission.isMissionStatus3()) {
                mission.setMissionStatus3(true);
                gainExp += 4000;
            }

            // 60분 달리기 미션 완료 조건
            if (dailyRunningTime >= 60 && !mission.isMissionStatus4()) {
                mission.setMissionStatus4(true);
                gainExp += 5000;
            }
            addExperience(activeCharacter, gainExp);
            dailyMissionRepository.save(mission);  // 미션 상태가 업데이트되면 저장
        }
    }

    // 주간 미션 평가
    public void evaluateWeeklyMissions(Long userId) {
        Running running = runningRepository.findByUserId(userId);
        List<GameCharacter> gameCharacters = gameCharacterRepository.findAllByUserId(userId);
        WeeklyMission mission = weeklyMissionRepository.findByUserId(userId);

        if (running == null) {
            return; // 러닝 기록이 없으면 종료
        }

        // 활성 캐릭터 탐색
        GameCharacter activeCharacter = findActiveCharacter(gameCharacters);

        if (activeCharacter == null) {
            return; // 레벨이 4인 캐릭터만 존재하면 종료
        }

        double weeklyDistance = running.getDistance(); // 주간 거리

        int gainExp = 0;

        // 주간 거리 15km 미션 완료 조건
        if (weeklyDistance >= 15 && !mission.isMissionStatus1()) {
            mission.setMissionStatus1(true);
            gainExp += 5000;
        }

        // 주간 거리 30km 미션 완료 조건
        if (weeklyDistance >= 30 && !mission.isMissionStatus2()) {
            mission.setMissionStatus2(true);
            gainExp += 7000;
        }

        // 주간 러닝 횟수 2회 미션 완료 조건
        if (mission.getRunningCount() >= 2 && !mission.isMissionStatus3()) {
            mission.setMissionStatus3(true);
            gainExp += 5000;
        }

        // 주간 러닝 횟수 4회 미션 완료 조건
        if (mission.getRunningCount() >= 4 && !mission.isMissionStatus4()) {
            mission.setMissionStatus4(true);
            gainExp += 8000;
        }
        addExperience(activeCharacter, gainExp);
        weeklyMissionRepository.save(mission);
    }

    // 캐릭터 경험치 추가 및 진화 조건 처리
    private void addExperience(GameCharacter character, int gainedExp) {
        int currentExp = character.getExperience();
        character.setExperience(currentExp + gainedExp);

        // 진화 조건 체크 및 레벨업
        if (character.getEvolutionLevel() == 0 && character.getExperience() >= 8000) {
            character.setEvolutionLevel(1);
            character.setExperience(character.getExperience() - 8000); // 진화 후 남은 경험치
        } else if (character.getEvolutionLevel() == 1 && character.getExperience() >= 15000) {
            character.setEvolutionLevel(2);
            character.setExperience(character.getExperience() - 15000); // 진화 후 남은 경험치
        } else if (character.getEvolutionLevel() == 2 && character.getExperience() >= 30000) {
            character.setEvolutionLevel(3);
            character.setExperience(character.getExperience() - 30000); // 진화 후 남은 경험치
        } else if (character.getEvolutionLevel() == 3 && character.getExperience() >= 50000) {
            character.setEvolutionLevel(4);
        }

        gameCharacterRepository.save(character); // 캐릭터 저장
    }

    // 활성 캐릭터 찾기
    private GameCharacter findActiveCharacter(List<GameCharacter> gameCharacters) {
        for (GameCharacter character : gameCharacters) {
            if (character.getEvolutionLevel() < 4) {
                return character; // 레벨이 4 미만인 캐릭터 반환
            }
        }
        return null; // 모든 캐릭터가 레벨 4 이상이면 null 반환
    }

    // 일일 미션 리셋 (매일 자정)
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void resetDailyMissions() {
        List<DailyMission> missions = dailyMissionRepository.findAll();

        for (DailyMission mission : missions) {
            mission.setMissionStatus1(false); // 미션 상태를 false로 리셋
            mission.setMissionStatus2(false);
            mission.setMissionStatus3(false);
            mission.setMissionStatus4(false);
            mission.setDailyRunningTime(0);
            mission.setDailyRunningDistance(0.0);
            dailyMissionRepository.save(mission); // 상태 저장
        }
    }
}
