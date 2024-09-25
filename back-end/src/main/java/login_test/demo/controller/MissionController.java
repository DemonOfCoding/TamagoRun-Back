package login_test.demo.controller;

import login_test.demo.dto.DailyMissionDto;
import login_test.demo.dto.WeeklyMissionDto;
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

    // 일일 미션 평가
    @PostMapping("/daily")
    public void evaluateDailyMissions(@RequestBody DailyMissionDto dailyMissionDto) {
        missionService.evaluateDailyMissions(dailyMissionDto.getUser_id());
    }

    // 주간 미션 데이터 누적 및 평가
    @PostMapping("/weekly")
    public void accumulateWeeklyData(@RequestBody WeeklyMissionDto weeklyMissionDto) {
        missionService.evaluateWeeklyMissions(weeklyMissionDto.getUser_id());
    }
}