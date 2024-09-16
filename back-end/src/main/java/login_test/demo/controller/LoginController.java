package login_test.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import login_test.demo.dto.ConfirmSetPasswordDto;
import login_test.demo.dto.EmailCheckDto;
import login_test.demo.dto.EmailRequestDto;
import login_test.demo.dto.LoginRequestDto;
import login_test.demo.model.User;
import login_test.demo.repository.UserRepository;
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
    private final UserRepository userRepository;

    //이메일 전송 (회원가입) || (비번 찾기)
    @PostMapping("/requestEmail")
    public ResponseEntity<String> requestEmail(@RequestBody EmailRequestDto emailRequestDto, HttpSession session) {
        String email = emailRequestDto.getEmail();

        boolean emailExists = userRepository.existsByEmail(email);

        if (emailExists) {
            return ResponseEntity.status(400).body("이미 존재하는 이메일 입니다.");
        }
        // 이메일로 인증 코드 전송
        String authCode = mailSendService.joinEmail(emailRequestDto.getEmail());

        // 세션에 인증 코드 저장
        session.setAttribute("authCode", authCode);
        session.setAttribute("email", emailRequestDto.getEmail()); // 이메일 정보만 세션에 저장

        return ResponseEntity.ok("이메일로 인증 코드를 발송했습니다.");
    }

    @PostMapping("/requestSetPassword")
    public ResponseEntity<String> requestSetPassword(@RequestBody LoginRequestDto loginRequestDto, HttpSession session) {
        String loginId = loginRequestDto.getLoginId();
        String email = loginRequestDto.getEmail();

        User user = userRepository.findByLoginIdAndEmail(loginId, email);
        if (user == null) {
            return ResponseEntity.status(400).body("아이디와 이메일이 일치하는 사용자가 없습니다.");
        }
        String authCode = mailSendService.joinEmail(email);

        session.setAttribute("authCode", authCode);
        session.setAttribute("email", email);

        return ResponseEntity.ok("이메일로 인증코드를 발송했습니다.");
    }

    // 이메일 인증 확인
    @PostMapping("/emailConfirm")
    public ResponseEntity<String> emailCheck(@RequestBody EmailCheckDto emailCheckDto, HttpSession session) {
        String authCode = (String) session.getAttribute("authCode");
        String email = (String) session.getAttribute("email");

        // 이메일과 인증 코드가 세션에 저장된 것과 일치하는지 확인
        if (authCode != null && authCode.equals(emailCheckDto.getAuthNum()) && email.equals(emailCheckDto.getEmail())) {
            session.setAttribute("emailVerified", true); // 이메일 인증 상태를 세션에 저장
            return ResponseEntity.ok("이메일 인증코드 확인 완료");
        } else {
            return ResponseEntity.status(400).body("인증번호가 올바르지 않습니다.");
        }
    }

    // 회원가입 요청 (이메일 인증 후)
    @PostMapping("/confirmSignUp")
    public ResponseEntity<String> confirmSignUp(@RequestBody User user, HttpSession session) {
        // 세션에서 인증 상태 확인
        Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");

        if (emailVerified != null && emailVerified) {
            // 세션에서 이메일 정보 가져오기
            String email = (String) session.getAttribute("email");
            user.setEmail(email); // 세션에 저장된 이메일 설정

            // 회원가입 처리
            loginService.signUp(user);

            // 세션에서 인증 정보 삭제
            session.removeAttribute("authCode");
            session.removeAttribute("email");
            session.removeAttribute("emailVerified");

            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } else {
            return ResponseEntity.status(400).body("이메일 인증이 필요합니다.");
        }
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequest, HttpServletRequest request) {

        System.out.println(loginRequest.getEmail() + loginRequest.getPassword() + loginRequest.getLoginId());

        if (loginService.login(loginRequest.getLoginId(), loginRequest.getPassword())) {
            System.out.println(loginRequest.getLoginId());

            //세션 생성 및 저장
            HttpSession session = request.getSession();
            session.setAttribute("userLogin", loginRequest.getLoginId());
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

    // 1. 아이디 중복 확인
    @GetMapping("/checkLoginId/{loginId}")
    public ResponseEntity<String> checkLoginIdDuplicate(@PathVariable("loginId") String loginId) {
        if (loginService.isLoginIdDuplicate(loginId)) {
            return ResponseEntity.status(400).body("아이디 중복");
        } else {
            return ResponseEntity.ok("아이디 사용 가능");
        }
    }

    //비밀번호 변경
    @PostMapping("/setPassword")
    public ResponseEntity<String> setPassword(@RequestBody LoginRequestDto loginRequestDto) {
        String loginId = loginRequestDto.getLoginId();
        String newPassword = loginRequestDto.getPassword();

        // 새로운 비밀번호를 password 필드에 저장
        loginRequestDto.setPassword(newPassword);

        // 새 비밀번호 설정
        if (loginService.setPassword(loginId, loginRequestDto.getPassword())) {
            return ResponseEntity.ok("비밀번호 변경 성공");
        } else { // 이메일 넣는게 없는데??
            return ResponseEntity.status(400).body("아이디 또는 이메일이 일치하지 않습니다.");
        }
    }

    //아이디, 이메일, 인증코드 확인
    @PostMapping("/confirmSetPassword")
    public ResponseEntity<String> confirmSetPassword(@RequestBody ConfirmSetPasswordDto requestDto, HttpSession session) {
        String loginId = requestDto.getLoginId();
        String email = requestDto.getEmail();
        String authNum = requestDto.getAuthNum();

        // 세션에서 인증 코드와 이메일을 가져옴
        String sessionAuthCode = (String) session.getAttribute("authCode");
        String sessionEmail = (String) session.getAttribute("email");

        // 데이터베이스에서 사용자 찾기
        User user = userRepository.findByLoginIdAndEmail(loginId, email);
        if (user == null) {
            return ResponseEntity.status(400).body("아이디와 이메일이 일치하는 사용자가 없습니다.");
        }

        // 이메일과 인증 코드가 세션에 저장된 것과 일치하는지 확인
        if (sessionAuthCode != null && sessionAuthCode.equals(authNum) && sessionEmail != null && sessionEmail.equals(email)) {
            session.setAttribute("emailVerified", true); // 이메일 인증 상태를 세션에 저장
            return ResponseEntity.ok("이메일 인증코드 확인 완료");
        } else {
            return ResponseEntity.status(400).body("인증번호가 올바르지 않습니다.");
        }
    }
}