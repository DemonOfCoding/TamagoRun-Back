package login_test.demo.controller;

import login_test.demo.dto.DailyMissionDto;
import login_test.demo.dto.SessionDto;
import login_test.demo.dto.WeeklyMissionDto;
import login_test.demo.model.User;
import login_test.demo.repository.UserRepository;
import login_test.demo.service.MissionService;
import login_test.demo.service.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mission")
public class MissionController {

    private final MissionService missionService;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    // 일일 미션 평가
    @PostMapping("/daily")
    public ResponseEntity<DailyMissionDto> evaluateDailyMissions(@RequestBody SessionDto sessionDto) {

        String loginId = redisUtil.getData(sessionDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (user.getId() == null || user.getId() <= 0)
            return ResponseEntity.badRequest().build();
        DailyMissionDto dailyMissionDto = missionService.evaluateDailyMissions(user.getId());

        return ResponseEntity.ok(dailyMissionDto); // DTO 객체를 반환
    }

    // 일일 미션 보상 획득
    @PostMapping("/dailyReward")
    public ResponseEntity<DailyMissionDto> getDailyMissionReward(@RequestBody SessionDto sessionDto) {
        String loginId = redisUtil.getData(sessionDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (user.getId() == null || user.getId() <= 0)
            return ResponseEntity.badRequest().build();
        DailyMissionDto dailyMissionDto = missionService.dailyMissionReward(user.getId());

        return ResponseEntity.ok(dailyMissionDto);
    }

    // 주간 미션 데이터 누적 및 평가
    @PostMapping("/weekly")
    public ResponseEntity<WeeklyMissionDto> accumulateWeeklyData(@RequestBody SessionDto sessionDto) {

        String loginId = redisUtil.getData(sessionDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (user.getId() == null || user.getId() <= 0)
            return ResponseEntity.badRequest().build();

        WeeklyMissionDto weeklyMissionDto = missionService.evaluateWeeklyMissions(user.getId());

        return ResponseEntity.ok(weeklyMissionDto);
    }

    // 주간 미션 보상 획득
    @PostMapping("/weeklyReward")
    public ResponseEntity<WeeklyMissionDto> getWeeklyMissionReward(@RequestBody SessionDto sessionDto) {
        String loginId = redisUtil.getData(sessionDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (user.getId() == null || user.getId() <= 0)
            return ResponseEntity.badRequest().build();

        WeeklyMissionDto weeklyMissionDto = missionService.weeklyMissionReward(user.getId());

        return ResponseEntity.ok(weeklyMissionDto);
    }
}