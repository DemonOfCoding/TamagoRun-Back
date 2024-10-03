package login_test.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameCharacterDto {
    private Long userId;
    private int species;
}
