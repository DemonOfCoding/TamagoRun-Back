package login_test.demo.repository;

import login_test.demo.model.Friendship;
import login_test.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsRepository  extends JpaRepository<Friendship, Long> {

    // 사용자의 친구 목록을 찾는 메서드
    List<Friendship> findByUser(User user);

    // 이미 친구 관계가 있는지 확인하는 메서드
    boolean existsByUserAndFriend(User user, User friend);

    // 친구 삭제
    void deleteByUserAndFriend(User user, User friend);

}
