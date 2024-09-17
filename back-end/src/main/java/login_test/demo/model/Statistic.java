package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    // 주 통계
    private int weeklyRunningTime;
    private Timestamp weeklyPacePerKm;
    private int weeklyRunningDistance;
    private int weeklyCalorie;

    // 월 통계
    private int monthlyRunningTime;
    private Timestamp monthlyPacePerKm;
    private int monthlyRunningDistance;
    private int monthlyCalorie;

    @ManyToOne
    @JoinColumn(name = "scheduleRecord_id", nullable = false)
    private ScheduleRecord scheduleRecord;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}