package login_test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendDto {
    private String friendId;
    private int kindOfCharacter;
    private int species;
    private int evolutionLevel;
}
