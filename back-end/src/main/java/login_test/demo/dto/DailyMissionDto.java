package login_test.demo.dto;

import lombok.Data;

@Data
public class DailyMissionDto {
    private Long userId; // user_id를 camelCase로 변경
    private double distance; // 주어진 거리
    private int runningTime; // 주어진 시간
}
