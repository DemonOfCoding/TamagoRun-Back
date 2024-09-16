package login_test.demo.service;

import jakarta.servlet.http.HttpSession;
import login_test.demo.dto.MypageInfoDto;
import login_test.demo.model.User;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageRetrieve {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    // 로그인 체크 후 로그인 사용자 정보 조회
    public MypageInfoDto getLoginUserInfo() {
        String loginId = (String) httpSession.getAttribute("userLogin");

        if (loginId == null) {
            throw new RuntimeException("사용자가 로그인되지 않았습니다.");
        }

        // 사용자 정보를 조회한다
        User user = userRepository.findByLoginId(loginId);

        if (user == null) {
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }

        MypageInfoDto mypageInfoDto = MypageInfoDto.builder()
                .loginId(user.getLoginId())
                .totalRunningTime(user.getTotalRunningTime())
                .totalRunningDistance(user.getTotalRunningDistance())
                .totalCalorie(user.getTotalCalorie())
                .overallAveragePace(user.getOverallAveragePace())
                .build();

        return mypageInfoDto;
    }
}