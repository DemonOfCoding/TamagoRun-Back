package login_test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunningDto {
    private String sessionId;
    private int dailyRunningTime;
    private int dailyAveragePace;
    private int dailyCalorie;
    private double dailyDistance;

    private List<CoordinateDTO> coordinates;
}
