package login_test.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MypageInfoDto {
    private String loginId;
    private int totalRunningTime;
    private double totalRunningDistance;
    private int totalCalorie;
    private int overallAveragePace;
}
