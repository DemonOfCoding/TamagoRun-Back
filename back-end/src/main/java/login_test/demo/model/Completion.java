package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Completion { // 미션 클리어 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 미션 또는 업적을 관리하는 공통 엔티티 필드
    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;  // null 가능 (업적일 경우)

    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;  // null 가능 (미션일 경우)

    private LocalDateTime completedAt;

    // 미션 또는 업적 클리어 상태
    private boolean isCleared;

    // 미션과 업적을 구분하는 플래그
    private boolean isMission;
}