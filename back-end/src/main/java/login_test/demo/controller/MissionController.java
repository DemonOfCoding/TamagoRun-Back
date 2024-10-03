package login_test.demo.controller;

import login_test.demo.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mission")
public class MissionController {

    private final MissionService missionService;

    // 일일 미션 평가
    @PostMapping("/daily")
    public ResponseEntity<String> evaluateDailyMissions(@RequestParam("userId") Long userId) {

        if (userId == null || userId <= 0)
            return ResponseEntity.badRequest().body("유효하지 않은 유저입니다.");
        missionService.evaluateDailyMissions(userId);

        return ResponseEntity.ok("일일 미션 평가 완료");
    }

    // 주간 미션 데이터 누적 및 평가
    @PostMapping("/weekly")
    public ResponseEntity<String> accumulateWeeklyData(@RequestParam("userId") Long userId) {
        if (userId == null || userId <= 0)
            return ResponseEntity.badRequest().body("유효하지 않은 유저입니다.");

        missionService.evaluateWeeklyMissions(userId);

        return ResponseEntity.ok("주간 미션 평가 완료");
    }
}