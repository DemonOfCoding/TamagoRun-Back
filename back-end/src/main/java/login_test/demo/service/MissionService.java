package login_test.demo.service;

import login_test.demo.model.DailyMission;
import login_test.demo.model.Running;
import login_test.demo.model.WeeklyMission;
import login_test.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final RunningRepository runningRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final WeeklyMissionRepository weeklyMissionRepository;

    // 일일 미션 평가
    public void evaluateDailyMissions(Long userId) {
        List<DailyMission> missions = dailyMissionRepository.findByUserId(userId);

        if (missions.isEmpty()) {
            return; // 미션이 없으면 종료
        }

        DailyMission dailyMission = missions.get(0); // Assume one DailyMission per user
        double dailyDistance = dailyMission.getDailyRunningDistance();
        int dailyRunningTime = dailyMission.getDailyRunningTime();

        // 미션 클리어 평가
        for (DailyMission mission : missions) {
            switch (mission.getId().intValue()) {
                case 1:
                    if (dailyDistance >= 3 && !mission.isMissionStatus()) {
                        completeDailyMission(mission);
                    }
                    break;
                case 2:
                    if (dailyDistance >= 5 && !mission.isMissionStatus()) {
                        completeDailyMission(mission);
                    }
                    break;
                case 3:
                    if (dailyRunningTime >= 30 && !mission.isMissionStatus()) {
                        completeDailyMission(mission);
                    }
                    break;
                case 4:
                    if (dailyRunningTime >= 60 && !mission.isMissionStatus()) {
                        completeDailyMission(mission);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void evaluateWeeklyMissions(Long userId) {
        Running running = runningRepository.findByUserId(userId);

        if (running == null) {
            return; // 러닝 기록이 없으면 종료
        }

        double weeklyDistance = running.getDistance(); // 주간 거리
        int weeklyRunningTime = running.getRunningTime(); // 주간 시간

        List<WeeklyMission> missions = weeklyMissionRepository.findByUserId(userId);

        // 하루에 한 번만 카운트할 변수를 추가합니다.
        boolean hasRunToday = false;

        // 오늘 날짜를 구합니다.
        LocalDate today = LocalDate.now();

        for (WeeklyMission mission : missions) {
            // 미션이 클리어되지 않았고, 오늘 뛴 기록이 있을 경우
            if (!mission.isMissionStatus() && !hasRunToday) {
                // 오늘의 기록이 있는지 확인
                LocalDate runningDate = running.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (runningDate.equals(today)) {
                    hasRunToday = true; // 오늘 뛴 기록이 있으므로 true로 설정
                    mission.setRunningCount(mission.getRunningCount() + 1); // runningCount 증가
                }
            }

            switch (mission.getId().intValue()) {
                case 1:
                    if (weeklyDistance >= 15 && !mission.isMissionStatus()) {
                        completeWeeklyMission(mission);
                    }
                    break;
                case 2:
                    if (weeklyDistance >= 30 && !mission.isMissionStatus()) {
                        completeWeeklyMission(mission);
                    }
                    break;
                case 3:
                    if (mission.getRunningCount() >= 2 && !mission.isMissionStatus()) {
                        completeWeeklyMission(mission);
                    }
                    break;
                case 4:
                    if (mission.getRunningCount() >= 4 && !mission.isMissionStatus()) {
                        completeWeeklyMission(mission);
                    }
                    break;
                default:
                    break;
            }
        }

        // 업데이트된 runningCount를 저장합니다.
        for (WeeklyMission mission : missions) {
            weeklyMissionRepository.save(mission);
        }
    }


    // 일일 미션 완료
    private void completeDailyMission(DailyMission mission) {
        mission.setMissionStatus(true);
        dailyMissionRepository.save(mission);
    }

    // 주간 미션 완료
    private void completeWeeklyMission(WeeklyMission mission) {
        mission.setMissionStatus(true);
        weeklyMissionRepository.save(mission);
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
}
