package login_test.demo.service;

import login_test.demo.dto.RunningDto;
import login_test.demo.model.Running;
import login_test.demo.model.User;
import login_test.demo.repository.RunningRepository;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RunningService {
    private final RunningRepository runningRepository;
    private final UserRepository userRepository;

    // 러닝 데이터 받기
    public void getRunningData(RunningDto runningDto) {
        // user_id를 통해 유저 프록시 객체를 가져옴
        User user = userRepository.getReferenceById(runningDto.getUser_id());

        // Running 엔티티에 User 설정
        Running running = Running.builder()
                .user(user)  // 유저 설정
                .dailyRunningTime(runningDto.getDailyRunningTime())
                .dailyAveragePace(runningDto.getDailyAveragePace())
                .dailyDistance(runningDto.getDailyDistance())
                .dailyCalorie(runningDto.getDailyCalorie())
                .coordinates(runningDto.getCoordinates())
                .build();

        runningRepository.save(running);
    }
}