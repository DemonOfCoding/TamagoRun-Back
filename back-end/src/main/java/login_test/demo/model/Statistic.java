package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    private int weeklyChart;

    private int monthChart;

    @ManyToOne
    @JoinColumn(name = "scheduleRecord_id", nullable = false)
    private ScheduleRecord scheduleRecord;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}