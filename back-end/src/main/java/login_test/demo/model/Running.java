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

    private double dailyRunningDistance;

    private int dailyRunningTime;

    private Timestamp pacePerKm;

    private int calorie;

    @OneToMany(mappedBy = "running")
    private List<ScheduleRecord> scheduleRecords;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
