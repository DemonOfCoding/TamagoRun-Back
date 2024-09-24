package login_test.demo.dto;

import lombok.Data;

@Data
public class WeeklyMissionDto {
    private Long userId; // user_id를 camelCase로 변경
    private double distance; // 누적 거리
    private int runningTime; // 누적 시간
}
