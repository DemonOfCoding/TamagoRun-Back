package login_test.demo.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import login_test.demo.model.User;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor // 생성자 자동 생성
public class LoginService {
    private final UserRepository userRepository;
    private final MailSendService mailSendService;
    private final RedisUtil redisUtil;

    //회원가입
    @Transactional
    public User signUp(User user) {
        System.out.println("Before Save: " + user.getLoginId());

        if (!isLoginIdDuplicate(user.getLoginId())) {
            User savedUser = userRepository.save(user); // 저장된 객체 반환
            System.out.println("After Save: User ID: " + savedUser.getLoginId()); // 저장 후 ID 확인
            return savedUser;
        }
        return null;
    }

    //로그인
    public String login(String loginId, String password, HttpServletRequest request) {
        // 데이터베이스에서 사용자를 찾는다
        User user = userRepository.findByLoginId(loginId);
        if (user == null) {
            System.out.println("사용자를 찾을 수 없습니다: " + loginId);
            return null;
        }

        if (user.getPassword().equals(password)) {
            //redis 에서 기존 세션 id 있는 확인
            String existingSessionId = redisUtil.getData(loginId);

            if (existingSessionId != null) {
                //기존 세션이 존재하면 그대로 반환
                return existingSessionId;
            } else {
                HttpSession session = request.getSession(true); // 강제로 세션 생성
                session.setAttribute("userLogin", loginId);

                session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 한달동안 유지

                // 만료 기간 설정
                redisUtil.setDataExpire(session.getId(), loginId, 30 * 24 * 60 * 60);

                return session.getId(); // 새 세션 id 반환
            }
        } else {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return null; // 비밀번호가 일치하지 않는 경우 null 반환
        }
    }

    //로그아웃
    public void logout(HttpSession session) {
        session.invalidate();
    }

    //아이디 중복 확인
    public boolean isLoginIdDuplicate(String loginId) {
        return userRepository.findByLoginId(loginId) != null;
    }

    // 아이디, 이메일 인증 후 인증코드 발송
    public boolean checkIdAndEmail(String loginId, String email) {
        User user = userRepository.findByLoginId(loginId); // 아이디 저장
        if (user == null || !user.getEmail().equals(email)) { // 아이디, 이메일 일치 여부 확인
            return false;
        }
        mailSendService.joinEmail(email); // 이메일 발송
        return true;
    }

    // 비밀번호 초기화
    public boolean setPassword(String loginId, String newPassword) {
        User user = userRepository.findByLoginId(loginId); // 아이디로 사용자 조회
        if (user != null) {
            user.setPassword(newPassword); // password 필드를 업데이트
            userRepository.save(user); // 변경 사항 저장
            return true;
        }
        return false; // 사용자를 찾지 못한 경우 false 반환
    }
}