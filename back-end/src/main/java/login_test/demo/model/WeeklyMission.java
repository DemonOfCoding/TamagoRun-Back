package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyMission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    // 미션 클리어 상태
    private boolean missionStatus;

    // 주간 뛴 횟수
    private int runningCount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void incrementRunningCount() {
        this.runningCount += 1;
    }
}
