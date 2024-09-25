package login_test.demo.service;

import login_test.demo.dto.RunningDto;
import login_test.demo.model.DailyMission;
import login_test.demo.model.Running;
import login_test.demo.model.User;
import login_test.demo.model.WeeklyMission;
import login_test.demo.repository.DailyMissionRepository;
import login_test.demo.repository.RunningRepository;
import login_test.demo.repository.UserRepository;
import login_test.demo.repository.WeeklyMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RunningService {
    private final RunningRepository runningRepository;
    private final UserRepository userRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final WeeklyMissionRepository weeklyMissionRepository;

    // 러닝 데이터 받기
    public void getRunningData(RunningDto runningDto) {
        // user_id를 통해 유저 프록시 객체를 가져옴
        User user = userRepository.getReferenceById(runningDto.getUser_id());

        // 현재 시간을 생성일로 설정
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Running 엔티티에 User 설정 및 누적 데이터 축적
        Running running = runningRepository.findByUserId(runningDto.getUser_id());

        // DailyMission 데이터 축적
        List<DailyMission> dailyMissions = dailyMissionRepository.findByUserId(runningDto.getUser_id());
        DailyMission dailyMission = dailyMissions.isEmpty() ? null : dailyMissions.get(0); // Assume one DailyMission per user

        if (running == null) {
            // 처음 데이터를 입력할 때
            running = Running.builder()
                    .user(user)
                    .runningTime(runningDto.getDailyRunningTime()) // 주간 누적 시간
                    .distance(runningDto.getDailyDistance())       // 주간 누적 거리
                    .calorie(runningDto.getDailyCalorie())
                    .averagePace(runningDto.getDailyAveragePace())
                    .coordinate(runningDto.getCoordinates())
                    .createdDate(currentTimestamp)
                    .build();
        } else {
            // 기존 데이터를 업데이트하여 누적
            running.setRunningTime(running.getRunningTime() + runningDto.getDailyRunningTime());
            running.setDistance(running.getDistance() + runningDto.getDailyDistance());
            running.setCalorie(running.getCalorie() + runningDto.getDailyCalorie());
        }

        // DailyMission 데이터 축적
        if (dailyMission == null) {
            // DailyMission이 없는 경우 새로 생성
            dailyMission = DailyMission.builder()
                    .user(user)
                    .missionStatus(false) // 초기 상태는 미션 미완료
                    .dailyRunningTime(runningDto.getDailyRunningTime())
                    .dailyRunningDistance(runningDto.getDailyDistance())
                    .build();
        } else {
            // 기존 DailyMission 업데이트
            dailyMission.setDailyRunningTime(dailyMission.getDailyRunningTime() + runningDto.getDailyRunningTime());
            dailyMission.setDailyRunningDistance(dailyMission.getDailyRunningDistance() + runningDto.getDailyDistance());
        }

        runningRepository.save(running);
        dailyMissionRepository.save(dailyMission);
    }

    // 주간 데이터 초기화 (매주 월요일 자정에 실행)
    @Scheduled(cron = "0 0 0 * * MON")
    public void resetWeeklyRunningData() {
        List<Running> runningRecords = runningRepository.findAll();
        for (Running running : runningRecords) {
            running.setRunningTime(0);
            running.setDistance(0);
            runningRepository.save(running);
        }

        // WeeklyMission 초기화
        List<WeeklyMission> weeklyMissions = weeklyMissionRepository.findAll();
        for (WeeklyMission mission : weeklyMissions) {
            mission.setRunningCount(0); // runningCount 초기화
            mission.setMissionStatus(false); // 필요 시 미션 상태도 초기화
            weeklyMissionRepository.save(mission);
        }
    }
}