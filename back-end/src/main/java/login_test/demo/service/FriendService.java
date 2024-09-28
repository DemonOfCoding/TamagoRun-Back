package login_test.demo.service;

import login_test.demo.model.Friendship;
import login_test.demo.model.User;
import login_test.demo.repository.FriendsRepository;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor //  클래스의 final 필드나 @NonNull이 붙은 필드에 대해 생성자를 자동으로 생성
public class FriendService {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    // 친구 추가 기능
    public void addFriend(String loginId, String friendId) {
        User user = userRepository.findByLoginId(loginId);
        User friend = userRepository.findByLoginId(friendId);

        if (user == null || friend == null) {
            throw new IllegalArgumentException("User or friend not found");
        }

        if (friendsRepository.existsByUserAndFriend(user, friend)) {
            throw new IllegalArgumentException("Already friends");
        }

        // 새로운 친구 관계 생성
        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);

        // 저장
        friendsRepository.save(friendship);
    }

    // 친구 목록 조회
    public List<Friendship> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return friendsRepository.findByUser(user);
    }

    // 친구 삭제 기능
    @Transactional
    public void deleteFriend(String loginId, String friendId) {
        // 로그인한 사용자 조회
        User user = userRepository.findByLoginId(loginId);
        User friend = userRepository.findByLoginId(friendId);

        if (user == null || friend == null) {
            throw new IllegalArgumentException("User or friend not found");
        }

        // 삭제할 친구 관계가 존재하는지 확인
        if (!friendsRepository.existsByUserAndFriend(user, friend)) {
            throw new IllegalArgumentException("Friendship does not exist");
        }

        // 친구 관계 삭제
        friendsRepository.deleteByUserAndFriend(user, friend);
    }


}
