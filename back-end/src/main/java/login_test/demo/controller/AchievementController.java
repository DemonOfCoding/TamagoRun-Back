package login_test.demo.controller;

import login_test.demo.dto.AchievementDto;
import login_test.demo.dto.SessionDto;
import login_test.demo.model.User;
import login_test.demo.repository.UserRepository;
import login_test.demo.service.AchievementService;
import login_test.demo.service.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/achievement")
public class AchievementController {

    private final AchievementService achievementService;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    // 업적 미션 평가
    @PostMapping("/evaluation")
    public ResponseEntity<AchievementDto> evaluateAchievement(@RequestBody SessionDto sessionDto) {

        String loginId = redisUtil.getData(sessionDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (user.getId() == null || user.getId() <= 0)
            return ResponseEntity.badRequest().build();

        AchievementDto achievementDto = achievementService.evaluationAchievement(user.getId());
        return ResponseEntity.ok(achievementDto);
    }

    // 업적 보상 획득
    @PostMapping("/achievementReward")
    public ResponseEntity<AchievementDto> getAchievementReward(@RequestBody SessionDto sessionDto) {
        String loginId = redisUtil.getData(sessionDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (user.getId() == null || user.getId() <= 0)
            return ResponseEntity.badRequest().build();

        AchievementDto achievementDto = achievementService.achievementReward(user.getId());

        return ResponseEntity.ok(achievementDto);
    }
}
