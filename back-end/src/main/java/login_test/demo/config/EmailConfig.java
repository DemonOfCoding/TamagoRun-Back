package login_test.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); // javamailsender 의 구현체 생성
        mailSender.setHost("smtp.gmail.com"); // 이메일 전송에 사용할 SMTP 서버 호스트를 설정
        mailSender.setPort(587); // 587로 포트를 지정
        mailSender.setUsername("opqieywi@gmail.com"); // 구글 계정
        mailSender.setPassword("wfce rnml wcnn tiio"); // 구글 앱 비밀번호

        Properties javaMailProps = new Properties();
        javaMailProps.put("mail.transport.protocol", "smtp"); // 프로토콜로 smtp 사용
        javaMailProps.put("mail.smtp.auth", "true"); // smtp 서버에 인증 필요
        javaMailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL 소켓 팩토리 클래스 사용
        javaMailProps.put("mail.smtp.starttls.enable", "true"); // STARTTLS(TLS를 시작하는 명령)를 사용하여 암호화된 통신을 활성화
        javaMailProps.put("mail.debug", "true"); // 디버깅 정보 출력
        javaMailProps.put("mail.smtp.ssl.trust", "smtp.gmail.com");  // smtp 서버의 ssl 인증서를 신뢰
        javaMailProps.put("mail.smtp.ssl.protocol", "TLSv1.2"); // 사용할 ssl 프로토콜 버젼

        mailSender.setJavaMailProperties(javaMailProps); // /mailSender에 우리가 만든 properties 넣고,
        return mailSender; // 빈으로 등록

    }
}
