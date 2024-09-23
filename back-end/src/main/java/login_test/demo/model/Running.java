package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Running {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    private int dailyRunningTime;
    private int dailyAveragePace;
    private int dailyCalorie;
    private double dailyDistance;

    // 지도 경로 받을 리스트
    @ElementCollection
    @CollectionTable(name = "running_coordinates", joinColumns = @JoinColumn(name = "running_id"))
    private List<Coordinate> coordinates;

}
