package login_test.demo.service;

import login_test.demo.dto.CoordinateDTO;
import login_test.demo.dto.RunningDto;
import login_test.demo.model.*;
import login_test.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RunningService {
    private final RunningRepository runningRepository;
    private final UserRepository userRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final WeeklyMissionRepository weeklyMissionRepository;
    private final AchievementRepository achievementRepository;
    private final RedisUtil redisUtil;
    private final CoordinateRepository coordinateRepository;

    // 달력에서 사용할 일별 러닝 데이터 받기
    public List<Coordinate> getDailyCoordinates(String sessionId, LocalDate date) {
        // 세션에서 로그인 ID 가져오기
        String loginId = redisUtil.getData(sessionId);
        // 사용자 조회
        User user = userRepository.findByLoginId(loginId);

        // 날짜 범위 계산
        Timestamp startOfDay = Timestamp.valueOf(date.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(date.plusDays(1).atStartOfDay().minusNanos(1));

        // 좌표 데이터 조회 및 반환
        return coordinateRepository.findAllByUserIdAndCreatedDateBetween(user.getId(), startOfDay, endOfDay);
    }

    // 러닝 데이터 받기
    public void getRunningData(RunningDto runningDto) {
        String loginId = redisUtil.getData(runningDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        WeeklyMission weeklyMission = weeklyMissionRepository.findByUserId(user.getId());

        // 현재 시간을 생성일로 설정
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // 좌표 데이터를 개별적으로 저장
        for (CoordinateDTO dtoCoordinate : runningDto.getCoordinates()) {
            Coordinate coordinate = new Coordinate();
            coordinate.setUser(user);
            coordinate.setX(dtoCoordinate.getX());
            coordinate.setY(dtoCoordinate.getY());
            coordinate.setCreatedDate(currentTimestamp);
            coordinateRepository.save(coordinate); // 독립적으로 Coordinate 저장
        }


        // Running 엔티티에 User 설정 및 누적 데이터 축적
        Running running = runningRepository.findByUserId(user.getId());

        // DailyMission 데이터 축적
        DailyMission dailyMission = dailyMissionRepository.findByUserId(user.getId());

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

        if (dailyMission.getDailyRunningTime() == 0 && dailyMission.getDailyRunningDistance() == 0.0) {
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

        user.setTotalCalorie(user.getTotalCalorie() + runningDto.getDailyCalorie());
        user.setTotalRunningDistance(user.getTotalRunningDistance() + runningDto.getDailyDistance());
        user.setTotalRunningTime(user.getTotalRunningTime() + runningDto.getDailyRunningTime());
        user.setOverallAveragePace((int) weightedAveragePace);

        runningRepository.save(running);
        weeklyMissionRepository.save(weeklyMission);
        dailyMissionRepository.save(dailyMission);
    }



    // 주간 데이터 초기화 (매주 월요일 자정에 실행)
    // cron (초(0~59), 분(0~59), 시간(0~23), 일(1~31), 월(1~12), 요일(0~7))
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void resetWeeklyRunningData() {
        List<Running> runningRecords = runningRepository.findAll();
        for (Running running : runningRecords) {
            running.setCreatedDate(null);
            running.setRunningTime(0);
            running.setDistance(0.0);
            running.setCalorie(0);
            running.setAveragePace(0);
            runningRepository.save(running);
        }

        // WeeklyMission 초기화
        List<WeeklyMission> weeklyMissions = weeklyMissionRepository.findAll();
        for (WeeklyMission mission : weeklyMissions) {
            mission.setRunningCount(0);
            mission.setMissionStatus1(false); // 필요 시 미션 상태도 초기화
            mission.setMissionStatus2(false);
            mission.setMissionStatus3(false);
            mission.setMissionStatus4(false);
            mission.setFlag1(false);
            mission.setFlag2(false);
            mission.setFlag3(false);
            mission.setFlag4(false);
            weeklyMissionRepository.save(mission);
        }
    }
}