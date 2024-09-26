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
public class GameCharacter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    private int experience;

    // 캐릭터 종족
    private int species;

    // 캐릭터 분류
    private int kindOfCharacter;

    private int evolutionCondition;

    private int characterLevel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}