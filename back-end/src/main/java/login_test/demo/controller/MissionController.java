package login_test.demo.controller;

import login_test.demo.dto.AchievementDto;
import login_test.demo.dto.MissionDto;
import login_test.demo.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mission")
public class MissionController {

    private final MissionService missionService;

    // 미션 추가 요청
    @PostMapping("/addMission")
    public void requestMission(@RequestBody MissionDto missionDto) {
        missionService.addMission(missionDto);
    }

    
    // 업적 추가 요청
    @PostMapping("/addAchievement")
    public void requestAchievement(@RequestBody AchievementDto achievementDto) {
        missionService.addAchievement(achievementDto);
    }


}
