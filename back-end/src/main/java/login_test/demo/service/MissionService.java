package login_test.demo.service;

import login_test.demo.dto.DailyMissionDto;
import login_test.demo.dto.WeeklyMissionDto;
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
    public DailyMissionDto evaluateDailyMissions(Long userId) {
        DailyMission dailyMission = dailyMissionRepository.findByUserId(userId);

        if (dailyMission == null) {
            return null;// 미션이 없으면 종료
        }

        double dailyDistance = dailyMission.getDailyRunningDistance();
        int dailyRunningTime = dailyMission.getDailyRunningTime();

        // 3km 미션 완료 조건
        if (dailyDistance >= 3 && !dailyMission.isMissionStatus1()) {
            dailyMission.setMissionStatus1(true);
        }

        // 5km 미션 완료 조건
        if (dailyDistance >= 5 && !dailyMission.isMissionStatus2()) {
            dailyMission.setMissionStatus2(true);
        }

        // 30분 달리기 미션 완료 조건
        if (dailyRunningTime >= 30 && !dailyMission.isMissionStatus3()) {
            dailyMission.setMissionStatus3(true);
        }

        // 60분 달리기 미션 완료 조건
        if (dailyRunningTime >= 60 && !dailyMission.isMissionStatus4()) {
            dailyMission.setMissionStatus4(true);
        }

        dailyMissionRepository.save(dailyMission);  // 미션 상태가 업데이트되면 저장

        // DailyMission 객체를 DTO로 변환하여 반환
        return new DailyMissionDto(
                dailyMission.isMissionStatus1(),
                dailyMission.isMissionStatus2(),
                dailyMission.isMissionStatus3(),
                dailyMission.isMissionStatus4(),
                dailyMission.isFlag1(),
                dailyMission.isFlag2(),
                dailyMission.isFlag3(),
                dailyMission.isFlag4()
        );
    }


    // 일일 미션 보상 획득
    public DailyMissionDto dailyMissionReward(Long userId) {
        DailyMission dailyMission = dailyMissionRepository.findByUserId(userId);
        List<GameCharacter> gameCharacters = gameCharacterRepository.findAllByUserId(userId);

        // 활성 캐릭터 탐색
        GameCharacter activeCharacter = findActiveCharacter(gameCharacters);

        if (activeCharacter == null) {
            return null; // 레벨이 4인 캐릭터만 존재하면 종료
        }

        int gainExp = 0;

        if (dailyMission.isMissionStatus1() && !dailyMission.isFlag1()) {
            gainExp += 50;
            dailyMission.setFlag1(true);
        }

        if (dailyMission.isMissionStatus2() && !dailyMission.isFlag2()) {
            gainExp += 50;
            dailyMission.setFlag2(true);
        }

        if (dailyMission.isMissionStatus3() && !dailyMission.isFlag3()) {
            gainExp += 50;
            dailyMission.setFlag3(true);
        }

        if (dailyMission.isMissionStatus4() && !dailyMission.isFlag4()) {
            gainExp += 50;
            dailyMission.setFlag4(true);
        }

        addExperience(activeCharacter, gainExp);

        return new DailyMissionDto(
                dailyMission.isMissionStatus1(),
                dailyMission.isMissionStatus2(),
                dailyMission.isMissionStatus3(),
                dailyMission.isMissionStatus4(),
                dailyMission.isFlag1(),
                dailyMission.isFlag2(),
                dailyMission.isFlag3(),
                dailyMission.isFlag4()
        );
    }

    // 주간 미션 평가
    public WeeklyMissionDto evaluateWeeklyMissions(Long userId) {
        Running running = runningRepository.findByUserId(userId);
        WeeklyMission weeklyMission = weeklyMissionRepository.findByUserId(userId);

        if (running == null) {
            return null; // 러닝 기록이 없으면 종료
        }

        double weeklyDistance = running.getDistance(); // 주간 거리

        // 주간 거리 15km 미션 완료 조건
        if (weeklyDistance >= 15 && !weeklyMission.isMissionStatus1()) {
            weeklyMission.setMissionStatus1(true);
        }

        // 주간 거리 30km 미션 완료 조건
        if (weeklyDistance >= 30 && !weeklyMission.isMissionStatus2()) {
            weeklyMission.setMissionStatus2(true);
        }

        // 주간 러닝 횟수 2회 미션 완료 조건
        if (weeklyMission.getRunningCount() >= 2 && !weeklyMission.isMissionStatus3()) {
            weeklyMission.setMissionStatus3(true);
        }

        // 주간 러닝 횟수 4회 미션 완료 조건
        if (weeklyMission.getRunningCount() >= 4 && !weeklyMission.isMissionStatus4()) {
            weeklyMission.setMissionStatus4(true);
        }

        weeklyMissionRepository.save(weeklyMission);

        return new WeeklyMissionDto(
                weeklyMission.isMissionStatus1(),
                weeklyMission.isMissionStatus2(),
                weeklyMission.isMissionStatus3(),
                weeklyMission.isMissionStatus4(),
                weeklyMission.isFlag1(),
                weeklyMission.isFlag2(),
                weeklyMission.isFlag3(),
                weeklyMission.isFlag4()
        );
    }

    // 주간 미션 보상 획득
    public WeeklyMissionDto weeklyMissionReward(Long userId) {
        WeeklyMission weeklyMission = weeklyMissionRepository.findByUserId(userId);
        List<GameCharacter> gameCharacters = gameCharacterRepository.findAllByUserId(userId);

        // 활성 캐릭터 탐색
        GameCharacter activeCharacter = findActiveCharacter(gameCharacters);

        if (activeCharacter == null) {
            return null; // 레벨이 4인 캐릭터만 존재하면 종료
        }

        int gainExp = 0;

        if (weeklyMission.isMissionStatus1() && !weeklyMission.isFlag1()) {
            gainExp += 200;
            weeklyMission.setFlag1(true);
        }

        if (weeklyMission.isMissionStatus2() && !weeklyMission.isFlag2()) {
            gainExp += 200;
            weeklyMission.setFlag2(true);
        }

        if (weeklyMission.isMissionStatus3() && !weeklyMission.isFlag3()) {
            gainExp += 200;
            weeklyMission.setFlag3(true);
        }

        if (weeklyMission.isMissionStatus4() && !weeklyMission.isFlag4()) {
            gainExp += 200;
            weeklyMission.setFlag4(true);
        }

        addExperience(activeCharacter, gainExp);

        return new WeeklyMissionDto(
                weeklyMission.isMissionStatus1(),
                weeklyMission.isMissionStatus2(),
                weeklyMission.isMissionStatus3(),
                weeklyMission.isMissionStatus4(),
                weeklyMission.isFlag1(),
                weeklyMission.isFlag2(),
                weeklyMission.isFlag3(),
                weeklyMission.isFlag4()
        );
    }

    // 캐릭터 경험치 추가
    private void addExperience(GameCharacter character, int gainedExp) {
        int currentExp = character.getExperience();
        character.setExperience(currentExp + gainedExp);

        gameCharacterRepository.save(character); // 경험치 저장
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
            mission.setFlag1(false);
            mission.setFlag2(false);
            mission.setFlag3(false);
            mission.setFlag4(false);
            mission.setDailyRunningTime(0);
            mission.setDailyRunningDistance(0.0);
            dailyMissionRepository.save(mission); // 상태 저장
        }
    }
}
