package login_test.demo.model;

import jakarta.persistence.*;
import lombok.*;

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
    // 캐릭터 단계
    private int evolutionLevel;
    private boolean evolutionFlag0;
    private boolean evolutionFlag1;
    private boolean evolutionFlag2;
    private boolean evolutionFlag3;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}