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
public class Achievement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    // 업적 상태
    private boolean achievementStatus1;
    private boolean achievementStatus2;
    private boolean achievementStatus3;
    private boolean achievementStatus4;
    private boolean achievementStatus5;
    private boolean achievementStatus6;
    private boolean achievementStatus7;
    private boolean achievementStatus8;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
