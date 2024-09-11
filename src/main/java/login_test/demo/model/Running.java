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

    @Column(nullable = false)
    private double dailyRunningDistance;

    @Column(nullable = false)
    private int dailyRunningTime;

    @Column(nullable = false)
    private Timestamp pacePerKm;

    @Column(nullable = false)
    private int calorie;

    @OneToMany(mappedBy = "running")
    private List<ScheduleRecord> scheduleRecords;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
