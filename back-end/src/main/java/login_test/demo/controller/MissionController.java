package login_test.demo.controller;

import login_test.demo.dto.SessionDto;
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
    public ResponseEntity<String> evaluateDailyMissions(@RequestBody SessionDto sessionDto) {

        String loginId = redisUtil.getData(sessionDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (user.getId() == null || user.getId() <= 0)
            return ResponseEntity.badRequest().body("유효하지 않은 유저입니다.");
        missionService.evaluateDailyMissions(user.getId());

        return ResponseEntity.ok("일일 미션 평가 완료");
    }

    // 주간 미션 데이터 누적 및 평가
    @PostMapping("/weekly")
    public ResponseEntity<String> accumulateWeeklyData(@RequestBody SessionDto sessionDto) {

        String loginId = redisUtil.getData(sessionDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (user.getId() == null || user.getId() <= 0)
            return ResponseEntity.badRequest().body("유효하지 않은 유저입니다.");

        missionService.evaluateWeeklyMissions(user.getId());

        return ResponseEntity.ok("주간 미션 평가 완료");
    }
}