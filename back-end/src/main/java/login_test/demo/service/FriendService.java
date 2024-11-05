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
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;
    private final RedisUtil redisUtil; // RedisUtil 주입

    // 친구 추가 기능
    public void addFriend(String sessionId, String friendId) {
        // Redis에서 sessionId로 loginId 조회
        if (sessionId == null) {
            throw new IllegalArgumentException("Invalid session ID");
        }

        // loginId로 User 객체 조회
        User user = userRepository.findByLoginId(sessionId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for the given session ID");
        }

        User friend = userRepository.findByLoginId(friendId);
        if (friend == null) {
            throw new IllegalArgumentException("Friend not found with login ID: " + friendId);
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
    public void deleteFriend(String sessionId, String friendId) {
        // Redis에서 sessionId로 loginId 조회

        if (sessionId == null) {
            throw new IllegalArgumentException("Invalid session ID");
        }

        // loginId로 User 객체 조회
        User user = userRepository.findByLoginId(sessionId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for the given session ID");
        }

        User friend = userRepository.findByLoginId(friendId);
        if (friend == null) {
            throw new IllegalArgumentException("Friend not found with login ID: " + friendId);
        }

        if (!friendsRepository.existsByUserAndFriend(user, friend)) {
            throw new IllegalArgumentException("Friendship does not exist");
        }

        // 친구 관계 삭제
        friendsRepository.deleteByUserAndFriend(user, friend);
    }
}