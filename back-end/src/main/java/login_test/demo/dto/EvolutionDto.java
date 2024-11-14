package login_test.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvolutionDto {
    private int species;
    private int kindOfCharacter;
    private int evolutionLevel;
}
