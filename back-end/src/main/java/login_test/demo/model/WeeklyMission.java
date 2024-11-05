package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyMission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    private int runningCount;

    // 미션 클리어 상태
    private boolean missionStatus1;
    private boolean missionStatus2;
    private boolean missionStatus3;
    private boolean missionStatus4;
    private boolean flag1;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
