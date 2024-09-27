package login_test.demo.controller;

import login_test.demo.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/achievement")
public class AchievementController {

    private final AchievementService achievementService;

    // 업적 미션 평가
    @PostMapping("/evaluation")
    public ResponseEntity<String> evaluateDailyMissions(@RequestParam("userId") Long userId) {

        if (userId == null || userId <= 0)
            return ResponseEntity.badRequest().body("유효하지 않은 사용자 아이디입니다.");

        achievementService.evaluationAchievement(userId);
        return ResponseEntity.ok("미션 평가가 완료되었습니다.");
    }
}
