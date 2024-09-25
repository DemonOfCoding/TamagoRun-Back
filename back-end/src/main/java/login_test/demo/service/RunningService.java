package login_test.demo.service;

import login_test.demo.dto.RunningDto;
import login_test.demo.model.Running;
import login_test.demo.repository.RunningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RunningService {
    private final RunningRepository runningRepository;

    // 러닝 데이터 받기
    public void getRunningData(RunningDto runningDto) {
        Running running = Running.builder()
                .dailyRunningTime(runningDto.getDailyRunningTime())
                .dailyAveragePace(runningDto.getDailyAveragePace())
                .dailyDistance(runningDto.getDailyDistance())
                .dailyCalorie(runningDto.getDailyCalorie())
                .coordinates(runningDto.getCoordinates())
                .build();

        runningRepository.save(running);
    }
}
