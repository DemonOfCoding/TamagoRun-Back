package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Running {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    // 기록 생성 날짜
    @Column(nullable = false, updatable = false)
    private Timestamp createdDate;

    private int runningTime;
    private int averagePace;
    private int calorie;
    private double distance;

    // 지도 경로 받을 리스트
    @ElementCollection
    @CollectionTable(name = "coordinate", joinColumns = @JoinColumn(name = "running_id"))
    private List<Coordinate> coordinate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
