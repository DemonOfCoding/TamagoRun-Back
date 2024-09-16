package login_test.demo.service;

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

    //회원가입
    @Transactional
    public boolean signUp(User user) {

        System.out.println("User Details - Login ID: " + user.getLoginId() + ", Password: " + user.getPassword() + ", Email: " + user.getEmail());

        if (!isLoginIdDuplicate(user.getLoginId())) {
            userRepository.save(user); // 사용자 정보 저장
            return true;
        }
        return false;
    }

    //로그인
    public boolean login(String loginId, String password) {
        // 데이터베이스에서 사용자를 찾는다
        User user = userRepository.findByLoginId(loginId);
        if (user == null) {
            System.out.println("사용자를 찾을 수 없습니다: " + loginId);
            return false;
        }

        if (user.getPassword().equals(password)) {
            return true; // 아이디와 비밀번호가 일치하면 true 반환
        } else {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return false; // 비밀번호가 일치하지 않으면 false 반환
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