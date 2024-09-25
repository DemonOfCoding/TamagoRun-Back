package login_test.demo.service;

import login_test.demo.dto.RunningDto;
import login_test.demo.model.Running;
import login_test.demo.model.User;
import login_test.demo.repository.RunningRepository;
import login_test.demo.repository.UserRepository;
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

    // 일일 러닝 데이터 삭제 (매일 자정)
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void deleteOldRunningRecords() {
        Timestamp oneDayAgo = new Timestamp(System.currentTimeMillis() - 86400000); // 1일 전 시간
        List<Running> oldRecords = runningRepository.findAllByCreatedDateBefore(oneDayAgo);
        runningRepository.deleteAll(oldRecords);
    }


    // 러닝 데이터 받기
    public void getRunningData(RunningDto runningDto) {
        // user_id를 통해 유저 프록시 객체를 가져옴
        User user = userRepository.getReferenceById(runningDto.getUser_id());

        // 현재 시간을 생성일로 설정
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Running 엔티티에 User 설정
        Running running = Running.builder()
                .user(user)  // 유저 설정
                .dailyRunningTime(runningDto.getDailyRunningTime())
                .dailyAveragePace(runningDto.getDailyAveragePace())
                .dailyDistance(runningDto.getDailyDistance())
                .dailyCalorie(runningDto.getDailyCalorie())
                .coordinate(runningDto.getCoordinates())
                .createdDate(currentTimestamp) // 생성일 설정
                .build();

        runningRepository.save(running);
    }
}