package login_test.demo.service;

import jakarta.servlet.http.HttpSession;
import login_test.demo.model.User;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor // 생성자 자동 생성
public class LoginService {
    private final UserRepository userRepository;

    //회원가입
    public boolean signUp(User user) {
        if (!isLoginIdDuplicate(user.getLoginId())) {
            userRepository.save(user); // 사용자 정보 저장
            return true;
        }
        return false;
    }

    //로그인
    public boolean login(String loginId, String password) {
        User user = userRepository.findByLoginId(loginId);
        return user != null && user.getPassword().equals(password);
    }

    //로그아웃
    public void logout(HttpSession session) {
        session.invalidate();
    }

    //아이디 중복 확인
    public boolean isLoginIdDuplicate(String loginid) {
        return userRepository.findByLoginId(loginid) != null;
    }
}
