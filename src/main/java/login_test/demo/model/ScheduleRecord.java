package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private int finalTime;

    @Column(nullable = false)
    private double finalDistance;

    @ManyToOne
    @JoinColumn(name = "running_id", nullable = false)
    private Running running;

    @OneToMany(mappedBy = "scheduleRecord")
    private List<Statistic> statistics;
}
