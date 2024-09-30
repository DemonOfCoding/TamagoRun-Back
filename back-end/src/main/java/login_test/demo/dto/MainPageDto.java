package login_test.demo.dto;

import lombok.Data;

@Data
public class MainPageDto {
    // 캐릭터, 캐릭터의 경험치, 캐릭터 레벨, 이번주 기록
    private String loginId;
    private int experience;
    private int kindOfCharacter;
    private int species;
    private int evolutionLevel;
    private int continuousDate;
}
