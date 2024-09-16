package login_test.demo.dto;

import lombok.Data;

@Data
public class ConfirmSetPasswordDto {
    private String loginId;
    private String email;
    private String authNum;
}
