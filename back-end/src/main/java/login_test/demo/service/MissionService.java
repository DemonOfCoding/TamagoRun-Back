package login_test.demo.service;

import login_test.demo.model.DailyMission;
import login_test.demo.model.Running;
import login_test.demo.model.WeeklyMission;
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

    // 일일 미션 평가 (데이터 누적 없음)
    public void evaluateDailyMissions(Long userId) {
        Running running = runningRepository.findByUserId(userId);

        // 만약 러닝 기록이 없다면 종료
        if (running == null) {
            return;
        }

        double distance = running.getDailyDistance();
        int runningTime = running.getDailyRunningTime();

        List<DailyMission> missions = dailyMissionRepository.findByUserId(userId);

        for (DailyMission mission : missions) {
            switch (mission.getId().intValue()) {
                case 1: // 하루에 3Km 이상 뛰기
                    if (distance >= 3 && !mission.isMissionStatus()) {
                        completeMission(mission);
                    }
                    break;
                case 2: // 하루에 5Km 이상 뛰기
                    if (distance >= 5 && !mission.isMissionStatus()) {
                        completeMission(mission);
                    }
                    break;
                case 3: // 30분 동안 뛰기
                    if (runningTime >= 30 && !mission.isMissionStatus()) {
                        completeMission(mission);
                    }
                    break;
                case 4: // 60분 동안 뛰기
                    if (runningTime >= 60 && !mission.isMissionStatus()) {
                        completeMission(mission);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // 주간 미션 누적 및 평가
    public void evaluateWeeklyMissions(Long userId) {
        Running running = runningRepository.findByUserId(userId);

        // 만약 러닝 기록이 없다면 종료
        if (running == null) {
            return;
        }

        double distance = running.getDailyDistance();
        int runningTime = running.getDailyRunningTime();

        List<WeeklyMission> missions = weeklyMissionRepository.findByUserId(userId);

        for (WeeklyMission mission : missions) {
            mission.setTotalDistance(mission.getTotalDistance() + distance);  // 거리 누적
            mission.setTotalTime(mission.getTotalTime() + runningTime);      // 시간 누적
            mission.setRunningCount(mission.getRunningCount() + 1);          // 횟수 누적

            switch (mission.getId().intValue()) {
                case 1: // 주간 15km 달성
                    if (mission.getTotalDistance() >= 15 && !mission.isMissionStatus()) {
                        completeMission(mission);
                    }
                    break;
                case 2: // 주간 30km 달성
                    if (mission.getTotalDistance() >= 30 && !mission.isMissionStatus()) {
                        completeMission(mission);
                    }
                    break;
                case 3: // 주간 2번 이상 러닝
                    if (mission.getRunningCount() >= 2 && !mission.isMissionStatus()) {
                        completeMission(mission);
                    }
                    break;
                case 4: // 주간 4번 이상 러닝
                    if (mission.getRunningCount() >= 4 && !mission.isMissionStatus()) {
                        completeMission(mission);
                    }
                    break;
                default:
                    break;
            }
            // 상태 업데이트 후 저장
            weeklyMissionRepository.save(mission);
        }
    }

    // 일일 미션 완료 처리
    private void completeMission(DailyMission mission) {
        mission.setMissionStatus(true); // 상태 업데이트
        dailyMissionRepository.save(mission); // 저장
    }

    // 주간 미션 완료 처리
    private void completeMission(WeeklyMission mission) {
        mission.setMissionStatus(true); // 상태 업데이트
        weeklyMissionRepository.save(mission); // 저장
    }

    // 일일 미션 리셋 (매일 자정)
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void resetDailyMissions() {
        List<DailyMission> missions = dailyMissionRepository.findAll();
        for (DailyMission mission : missions) {
            mission.setMissionStatus(false); // 미션 상태를 false로 리셋
            dailyMissionRepository.save(mission); // 상태 저장
        }
    }

    // 주간 미션 리셋 (매주 월요일 자정)
    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 자정에 실행
    public void resetWeeklyMissions() {
        List<WeeklyMission> missions = weeklyMissionRepository.findAll();
        for (WeeklyMission mission : missions) {
            mission.setMissionStatus(false); // 미션 상태를 false로 리셋
            weeklyMissionRepository.save(mission); // 상태 저장
        }
    }
}
