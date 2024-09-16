package login_test.demo.controller;

import jakarta.validation.Valid;
import login_test.demo.dto.EmailCheckDto;
import login_test.demo.dto.EmailRequestDto;
import login_test.demo.service.MailSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailSendService mailService; // 이메일 전송 작업을 담당하는 서비스 클래스(생성자 주입을 통해 초기화됨), mailsendservice 가 스프링 빈으로 등록 되어야함

    @PostMapping("/mailSend") // 클라이언트로 부터 post 방식의 http 요청을 처리하는 메서드, 엔드포인트인 mailSend 로 부터 들어오는 post 요청(이메일 정보 저장, 해당 이메일로 인증 메일 발송) 처리
    public String mailSend(@RequestBody @Valid EmailRequestDto emailDto) { // 전송된 이메일 데이터를 담는 DTO, 유효성 검사를 통해 이메일 주소 형식이 올바른지 검사
        System.out.println("이메일 인증 이메일: " + emailDto.getEmail());
        return mailService.joinEmail(emailDto.getEmail());
        // emailDto.getEmail(): 요청으로 받은 EmailRequestDto 객체에서 이메일 주소를 추출
        // mailService.joinEmail(email): joinEmail 메서드가 호출, 이 메서드는 이메일 전송 로직을 포함, 파라미터로 전달된 이메일 주소로 인증 메일을 보냄
        // return: 메서드가 이메일 전송 결과로 반환한 문자열을 클라이언트에게 응답으로 전달
    }

    @PostMapping("/mailAuthCheck")
    public String AuthCheck(@RequestBody @Valid EmailCheckDto emailCheckDto) {
        Boolean Checked = mailService.checkAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum());
        if (Checked) {
            return "ok";
        } else {
            throw new NullPointerException("뭔가 잘못됨 ㅋ");
        }
    }
}
