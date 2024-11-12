package login_test.demo.dto;

import login_test.demo.model.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<Coordinate> coordinates;
}
