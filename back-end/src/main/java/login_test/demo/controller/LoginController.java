package login_test.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import login_test.demo.dto.*;
import login_test.demo.model.GameCharacter;
import login_test.demo.model.Statistic;
import login_test.demo.model.User;
import login_test.demo.repository.GameCharacterRepository;
import login_test.demo.repository.StatisticRepository;
import login_test.demo.repository.UserRepository;
import login_test.demo.service.GameCharacterService;
import login_test.demo.service.LoginService;
import login_test.demo.service.MailSendService;
import login_test.demo.service.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class LoginController {

    private final LoginService loginService;
    private final MailSendService mailSendService;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final GameCharacterService gameCharacterService;


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

        return ResponseEntity.ok("이메일로 인증 코드를 발송했습니다." + authCode);
    }

    // 비밀번호 찾기할 때 (한 사용자의 아이디와 이메일인지 확인)
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

            // 회원가입 처리 후 저장된 User 객체 반환
            User savedUser = loginService.signUp(user);
            System.out.println(savedUser + " = savedUser");

            if (savedUser == null || savedUser.getId() == null) {
                return ResponseEntity.status(500).body("회원가입 후 사용자 ID가 설정되지 않았습니다.");
            }

            GameCharacterDto gameCharacterDto = GameCharacterDto.builder()
                    .userId(savedUser.getId())
                    .species(0)
                    .build();

            gameCharacterService.generateCharacter(gameCharacterDto);
            System.out.println(gameCharacterDto.getUserId());
            System.out.println(gameCharacterDto.getSpecies());

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
        // 서비스 계층에서 로그인 처리 및 세션 관리
        String sessionId = loginService.login(loginRequest.getLoginId(), loginRequest.getPassword(), request);

        if (sessionId != null) {
            // 로그인 성공 시 세션 ID 반환
            return ResponseEntity.ok("로그인 성공, 세션 ID: " + sessionId);
        } else {
            // 로그인 실패 시 에러 메시지 반환
            return ResponseEntity.status(401).body("로그인 실패 : 유효하지 않은 아이디 또는 비밀번호");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        loginService.logout(session);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/checkSession")
    public ResponseEntity<String> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String cookieHeader = request.getHeader("Cookie");

        // 쿠키에서 JSESSIONID 찾기
        String sessionId = null;
        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                cookie = cookie.trim();
                if (cookie.startsWith("JSESSIONID=")) {
                    sessionId = cookie.substring("JSESSIONID=".length());
                    break;
                }
            }
        }

        // 세션이 없을 때 Redis에서 세션 ID로 loginId 조회
        if (session == null && sessionId != null) {
            // Redis에서 sessionId를 통해 loginId를 가져옴
            String loginId = redisUtil.getData(sessionId);

            if (loginId != null) {
                // Redis에서 유효한 loginId를 찾았을 경우 세션을 새로 생성하고 로그인 정보 설정
                session = request.getSession(true);
                session.setAttribute("userLogin", loginId);

                User user = userRepository.findByLoginId(loginId);
                if (user != null) {
                    return ResponseEntity.ok("자동 로그인된 사용자: " + user.getLoginId());
                } else {
                    return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
                }
            } else {
                return ResponseEntity.status(401).body("로그인되지 않은 상태입니다.");
            }
        }

        // 세션이 유효할 경우
        if (session != null && session.getAttribute("userLogin") != null) {
            String loginId = (String) session.getAttribute("userLogin");
            User user = userRepository.findByLoginId(loginId);
            if (user != null) {
                return ResponseEntity.ok("현재 로그인된 사용자: " + user.getLoginId());
            } else {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }
        } else {
            return ResponseEntity.status(401).body("로그인되지 않은 상태입니다.");
        }
    }

    // 아이디 중복 확인
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