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
public class Mission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    private String title;

    private String content;

    // 미션 클리어 상태
    private boolean missionStatus;
    // 미션 보상
    private int missionReward;

    // 이메일 인증
    @Column(nullable = false)
    private boolean emailVerified = false;

    // 이메일 인증 토큰
    @Column(nullable = false)
    private String emailVerificationToken;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "mission")
    private List<GameCharacter> gameCharacters;
}