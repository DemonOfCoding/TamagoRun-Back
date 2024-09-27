package login_test.demo.controller;

import login_test.demo.dto.DailyMissionDto;
import login_test.demo.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/achievement")
public class AchievementController {

    private final AchievementService achievementService;

    // 업적 미션 평가
    @PostMapping("/evaluation")
    public void evaluateDailyMissions(@RequestParam("userId") Long userId) {

        achievementService.evaluationAchievement(userId);
    }
}
