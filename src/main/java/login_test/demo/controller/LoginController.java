package login_test.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import login_test.demo.dto.EmailCheckDto;
import login_test.demo.dto.LoginRequestDto;
import login_test.demo.model.User;
import login_test.demo.service.LoginService;
import login_test.demo.service.MailSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class LoginController {

    private final LoginService loginService;
    private  final MailSendService mailSendService;

    // 아이디 중복 확인 및 이메일 인증 코드 전송
    @PostMapping("/requestSignUp")
    public ResponseEntity<String> requestSignUp(@RequestBody User user, HttpSession session) {
        // 아이디 중복 확인
        if (!loginService.isLoginIdDuplicate(user.getLoginId())) {
            // 중복이 아닐 경우 이메일로 인증 코드 전송
            String authCode = mailSendService.joinEmail(user.getEmail());
            session.setAttribute("authCode", authCode); // 세션에 인증 코드 저장
            session.setAttribute("userInfo", user); // 사용자 정보를 세션에 저장
            return ResponseEntity.ok("이메일로 인증 코드를 발송했습니다.");
        } else {
            return ResponseEntity.status(400).body("아이디가 중복되었습니다.");
        }
    }

    // 이메일 인증 번호 확인 및 회원가입 처리
    @PostMapping("/confirmSignUp")
    public ResponseEntity<String> confirmSignUp(@RequestBody EmailCheckDto emailCheckDto, HttpSession session) {
        String authCode = (String) session.getAttribute("authCode"); // 세션에서 인증 코드 가져옴
        User user = (User) session.getAttribute("userInfo"); // 세션에서 사용자 정보 가져옴

        if (authCode != null && authCode.equals(emailCheckDto.getAuthNum())) {
            // 인증 성공 시 회원가입 처리
            loginService.signUp(user);
            session.removeAttribute("authCode"); // 인증 코드 삭제
            session.removeAttribute("userInfo"); // 사용자 정보 삭제
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } else {
            return ResponseEntity.status(400).body("인증번호가 올바르지 않습니다.");
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        if (loginService.login(loginRequestDto.getLoginId(), loginRequestDto.getPassword())) {
            //세션 생성 및 저장
            HttpSession session = request.getSession();
            session.setAttribute("userLogin", loginRequestDto.getLoginId());
            return ResponseEntity.ok("로그인 성공, 세션 ID" + session.getId());
        }
        else {
            return ResponseEntity.status(401).body("로그인 실패 : 유효하지 않은 아이디 또는 비밀번호");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        loginService.logout(session);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 아이디 중복 체크
    @GetMapping("/checkLoginId/{loginId}")
    public ResponseEntity<String> checkLoginIdDuplicate(@PathVariable("loginId") String loginId) {
        if (loginService.isLoginIdDuplicate(loginId)) {
            return ResponseEntity.status(400).body("아이디 중복");
        } else {
            return ResponseEntity.ok("아이디 사용 가능");
        }
    }

}