package login_test.demo.controller;

import jakarta.servlet.http.HttpSession;
import login_test.demo.dto.MainPageDto;
import login_test.demo.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mainPage")
public class MainPageController {

    private final MainPageService mainPageService;
    private final HttpSession httpSession;

    @GetMapping("/check")
    public ResponseEntity<?> checkCharacter() {
        String loginId = (String) httpSession.getAttribute("userLogin");

        if (loginId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 된 사용자가 없습니다.");
        }

        MainPageDto mainPageDto = mainPageService.checkCharacter(loginId);

        return ResponseEntity.ok(mainPageDto);
    }
}
