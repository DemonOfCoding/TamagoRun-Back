package login_test.demo.controller;

import jakarta.servlet.http.HttpSession;
import login_test.demo.dto.MypageInfoDto;
import login_test.demo.service.MypageRetrieve;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final MypageRetrieve mypageRetrieve;
    private final HttpSession httpSession;

    // 마이페이지 정보 조회
    @GetMapping("/info")
    public ResponseEntity<MypageInfoDto> getUserInfo() {
        try {
            MypageInfoDto userInfo = mypageRetrieve.getLoginUserInfo();
            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) {
            // 로그인되지 않았거나 사용자 정보를 찾을 수 없는 경우
            return ResponseEntity.status(401).body(null);
        }
    }
}