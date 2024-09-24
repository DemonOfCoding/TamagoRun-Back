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

    private String title;

    private String content;

    // 미션 클리어 상태
    private boolean missionStatus;

    // 미션 보상
    private int missionReward;

    // 주간 누적 거리
    private double totalDistance;

    // 주간 누적 시간
    private int totalTime;

    // 주간 뛴 횟수
    private int runningCount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "weeklymission")
    private List<GameCharacter> gameCharacters;
}
