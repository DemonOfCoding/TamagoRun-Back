package login_test.demo.service;

import login_test.demo.model.*;
import login_test.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Optional<GameCharacter> gameCharacter = gameCharacterRepository.findByUserId(userId);
        if (missions.isEmpty()) {
            return; // 미션이 없으면 종료
        }

        DailyMission dailyMission = missions.get(0); // Assume one DailyMission per user
        double dailyDistance = dailyMission.getDailyRunningDistance();
        int dailyRunningTime = dailyMission.getDailyRunningTime();

        // 미션 클리어 평가
        for (DailyMission mission : missions) {
            // 3km 미션 완료 조건
            if (dailyDistance >= 3 && !mission.isMissionStatus1()) {
                mission.setMissionStatus1(true);
            }

            // 5km 미션 완료 조건
            if (dailyDistance >= 5 && !mission.isMissionStatus2()) {
                mission.setMissionStatus2(true);
            }

            // 30분 달리기 미션 완료 조건
            if (dailyRunningTime >= 30 && !mission.isMissionStatus3()) {
                mission.setMissionStatus3(true);
            }

            // 60분 달리기 미션 완료 조건
            if (dailyRunningTime >= 60 && !mission.isMissionStatus4()) {
                mission.setMissionStatus4(true);
            }

            dailyMissionRepository.save(mission);  // 미션 상태가 업데이트되면 저장
        }
    }

    // 주간 미션 평가
    public void evaluateWeeklyMissions(Long userId) {
        Running running = runningRepository.findByUserId(userId);

        if (running == null) {
            return; // 러닝 기록이 없으면 종료
        }

        double weeklyDistance = running.getDistance(); // 주간 거리

        WeeklyMission mission = weeklyMissionRepository.findByUserId(userId);


        // 주간 거리 15km 미션 완료 조건
        if (weeklyDistance >= 15 && !mission.isMissionStatus1()) {
            mission.setMissionStatus1(true);
        }

        // 주간 거리 30km 미션 완료 조건
        if (weeklyDistance >= 30 && !mission.isMissionStatus2()) {
            mission.setMissionStatus2(true);
        }

        // 주간 러닝 횟수 2회 미션 완료 조건
        if (mission.getRunningCount() >= 2 && !mission.isMissionStatus3()) {
            mission.setMissionStatus3(true);
        }

        // 주간 러닝 횟수 4회 미션 완료 조건
        if (mission.getRunningCount() >= 4 && !mission.isMissionStatus4()) {
            mission.setMissionStatus4(true);
        }

        weeklyMissionRepository.save(mission);
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
