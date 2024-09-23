package login_test.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity // DB 테이블과 매핑(테이블 자동 생성, 수정, 삭제)
@Data // getter, setter 등등
@AllArgsConstructor //  모든 필드 값을 매개변수로 받는 생성자를 자동으로 생성
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동 생성
public class Friendship {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    private Long id;

    @ManyToOne // 친구 관계를 맺는 사용자, 여러 개의 친구 관계를 가질 수 있지만, 각 관계는 하나의 User에 매핑
    @JoinColumn(name = "user_id", nullable = false) // 외래키 지정, null값 X ()
    private User user;

    @ManyToOne // 친구 관계의 상대방 사용자,
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;
}
