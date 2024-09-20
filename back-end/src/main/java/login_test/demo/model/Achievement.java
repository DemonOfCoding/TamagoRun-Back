package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    private String title;

    private String content;

    // 업적 상태
    private boolean achievementStatus;
    // 업적 보상
    private int achievementReward;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
