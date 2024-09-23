package login_test.demo.dto;

import lombok.Data;

@Data
public class MissionDto {

    private Long user_id;
    private String title;
    private String content;
    private int missionReward;
}
