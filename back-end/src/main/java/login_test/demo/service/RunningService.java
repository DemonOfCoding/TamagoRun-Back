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
        WeeklyMission weeklyMission = weeklyMissionRepository.findByUserId(runningDto.getUser_id());

        // 현재 시간을 생성일로 설정
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Running 엔티티에 User 설정 및 누적 데이터 축적
        Running running = runningRepository.findByUserId(runningDto.getUser_id());

        // DailyMission 데이터 축적
        List<DailyMission> dailyMissions = dailyMissionRepository.findByUserId(runningDto.getUser_id());
        DailyMission dailyMission = dailyMissions.isEmpty() ? null : dailyMissions.get(0); // Assume one DailyMission per user

// 기존 거리 및 평균 페이스 가져오기, 전체 평균 페이스
        double previousDistance = user.getTotalRunningDistance();
        double previousAveragePace = user.getOverallAveragePace();
        // 주 평균 페이스
        double previousDistance2 = (running != null) ? running.getDistance() : 0.0;
        double previousAveragePace2 = (running != null) ? running.getAveragePace() : 0;

        double newDistance = runningDto.getDailyDistance();
        int newPace = runningDto.getDailyAveragePace();

        // 전체 평균 페이스 계산 (처음 받은 데이터일 경우 처리)
        double totalDistance;
        double weightedAveragePace;
        if (previousDistance == 0) {
            // 처음 데이터일 경우는 그대로 저장
            totalDistance = newDistance;
            weightedAveragePace = newPace;
        } else {
            // 기존 값이 있을 경우 가중 평균 계산
            totalDistance = previousDistance + newDistance;
            weightedAveragePace = (previousAveragePace * previousDistance + newPace * newDistance) / totalDistance;
        }

        // 주 평균 페이스 계산 (처음 받은 데이터일 경우 처리)
        double weeklyDistance;
        double weeklyWeightedAveragePace;
        if (previousDistance2 == 0) {
            // 처음 데이터일 경우는 그대로 저장
            weeklyDistance = newDistance;
            weeklyWeightedAveragePace = newPace;
        } else {
            // 기존 값이 있을 경우 가중 평균 계산
            weeklyDistance = previousDistance2 + newDistance;
            weeklyWeightedAveragePace = (previousAveragePace2 * previousDistance2 + newPace * newDistance) / weeklyDistance;
        }

        if (running == null) {
            // 처음 데이터를 입력할 때
            running = Running.builder()
                    .user(user)
                    .runningTime(runningDto.getDailyRunningTime()) // 주간 누적 시간
                    .distance(runningDto.getDailyDistance())       // 주간 누적 거리
                    .calorie(runningDto.getDailyCalorie())
                    .averagePace(newPace)
                    .coordinate(runningDto.getCoordinates())
                    .createdDate(currentTimestamp)
                    .build();
        } else {
            // 기존 데이터를 업데이트하여 누적
            running.setRunningTime(running.getRunningTime() + runningDto.getDailyRunningTime());
            running.setDistance(running.getDistance() + runningDto.getDailyDistance());
            running.setCalorie(running.getCalorie() + runningDto.getDailyCalorie());
            // 새로 계산된 가중 평균 페이스를 저장
            running.setAveragePace((int) weeklyWeightedAveragePace);
        }

        // DailyMission 데이터 축적
        if (dailyMission == null) {
            // DailyMission이 없는 경우 새로 생성
            dailyMission = DailyMission.builder()
                    .user(user)
                    .missionStatus1(false) // 초기 상태는 미션 미완료
                    .missionStatus2(false)
                    .missionStatus3(false)
                    .missionStatus4(false)
                    .dailyRunningTime(runningDto.getDailyRunningTime())
                    .dailyRunningDistance(runningDto.getDailyDistance())
                    .build();
        } else {
            if(dailyMission.getDailyRunningTime() == 0 && dailyMission.getDailyRunningDistance() == 0.0)
            {
                // 러닝 카운트
                int updatedCount1 = user.getTotalRunningCount() + 1;
                int updatedCount2 = weeklyMission.getRunningCount() + 1;
                user.setTotalRunningCount(updatedCount1);
                weeklyMission.setRunningCount(updatedCount2);

                // 변경된 값을 저장
                weeklyMissionRepository.save(weeklyMission);
                userRepository.save(user);
            } // 일일 데이터가 없으면 처음 뛰는 것이므로 러닝 카운터를 1 상승 시킴

            // 기존 DailyMission 업데이트
            dailyMission.setDailyRunningTime(dailyMission.getDailyRunningTime() + runningDto.getDailyRunningTime());
            dailyMission.setDailyRunningDistance(dailyMission.getDailyRunningDistance() + runningDto.getDailyDistance());
        }

        user.setTotalCalorie(user.getTotalCalorie() + runningDto.getDailyCalorie());
        user.setTotalRunningDistance(user.getTotalRunningDistance() + runningDto.getDailyDistance());
        user.setTotalRunningTime(user.getTotalRunningTime() + runningDto.getDailyRunningTime());
        user.setOverallAveragePace((int) weightedAveragePace);

        runningRepository.save(running);
        dailyMissionRepository.save(dailyMission);
    }

    // 주간 데이터 초기화 (매주 월요일 자정에 실행)
    // cron (초(0~59), 분(0~59), 시간(0~23), 일(1~31), 월(1~12), 요일(0~7))
    @Scheduled(cron = "0 0 0 * * MON")
    public void resetWeeklyRunningData() {
        List<Running> runningRecords = runningRepository.findAll();
        for (Running running : runningRecords) {
            running.setRunningTime(0);
            running.setDistance(0.0);
            running.setCalorie(0);
            running.setAveragePace(0);
            runningRepository.save(running);
        }

        // WeeklyMission 초기화
        List<WeeklyMission> weeklyMissions = weeklyMissionRepository.findAll();
        for (WeeklyMission mission : weeklyMissions) {
            mission.setMissionStatus1(false); // 필요 시 미션 상태도 초기화
            mission.setMissionStatus2(false);
            mission.setMissionStatus3(false);
            mission.setMissionStatus4(false);
            weeklyMissionRepository.save(mission);
        }
    }
}