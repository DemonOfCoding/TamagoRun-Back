package login_test.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class MailSendService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private String authNumber; // 문자열로 처리하는 것이 유연함

    // 인증번호 생성
    public void makeRandomNumber() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10)); // 6자리 랜덤 숫자 생성
        }
        authNumber = randomNumber.toString(); // 문자열로 변환
    }

    // 인증번호 확인
    public boolean checkAuthNum(String email, String authNum) {
        String storedEmail = redisUtil.getData(authNum); // Redis에서 인증번호에 해당하는 이메일 가져오기
        return storedEmail != null && storedEmail.equals(email); // 인증번호와 이메일이 일치하는지 확인
    }

    // 이메일 전송 후 Redis에 저장
    public String joinEmail(String email) {
        makeRandomNumber(); // 인증번호 생성
        String setFrom = "opqieywi@gmail.com";
        String toMail = email;
        String title = "회원 가입 인증 이메일 입니다.";
        String content =
                "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요";

        mailSend(setFrom, toMail, title, content);
        redisUtil.setDataExpire(authNumber, email, 300L); // Redis에 5분(300초)간 유효하게 인증번호 저장
        return authNumber; // 생성된 인증번호 반환
    }

    // 이메일 발송 로직
    private void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage(); // JavaMailSender 객체를 사용하여 MimeMessage 객체 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8"); // multipart 및 인코딩 설정

            helper.setFrom(setFrom); // 발신자 설정
            helper.setTo(toMail); // 수신자 설정
            helper.setSubject(title); // 제목 설정
            helper.setText(content, true); // 내용 및 HTML 설정

            mailSender.send(message); // 이메일 발송

        } catch (MessagingException e) {
            System.out.println("이메일 전송 실패: " + e.getMessage()); // 에러 메시지를 로그로 출력
        }
    }
}