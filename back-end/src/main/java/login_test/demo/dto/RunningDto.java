package login_test.demo.dto;

import login_test.demo.model.Coordinate;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class RunningDto {
    private int dailyRunningTime;
    private int dailyAveragePace;
    private int dailyCalorie;
    private double dailyDistance;
    private List<Coordinate> coordinates;
}
