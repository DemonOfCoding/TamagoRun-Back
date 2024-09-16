package login_test.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private int totalRunningTime;

    private double totalRunningDistance;

    // 연속으로 뛴 일수
    private int continuousDate;

    // 뛰었는지 체크
    private boolean runningRecord;

    // 완료 미션
    private int clearMission;

    private int totalCalorie;

    // 전체 평균 페이스
    private int overallAveragePace;

    @OneToMany(mappedBy = "user")
    private List<GameCharacter> users;

    @OneToMany(mappedBy = "user")
    private List<Mission> missions;

    @OneToMany(mappedBy = "user")
    private List<Statistic> statistics;

    @OneToMany(mappedBy = "user")
    private List<Running> runnings;
}