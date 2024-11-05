package login_test.demo.service;

import login_test.demo.model.Friendship;
import login_test.demo.model.GameCharacter;
import login_test.demo.model.User;
import login_test.demo.repository.FriendsRepository;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import login_test.demo.dto.FriendDto;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

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
    public List<FriendDto> getFriends(String loginId) {
        User user = userRepository.findByLoginId(loginId);
        List<Friendship> friendships = friendsRepository.findByUser(user);

        // 디버그용 로그 추가: 반환된 친구 관계의 수를 확인
        System.out.println("Friendships found: " + friendships.size());

        // `FriendDTO`로 변환하여 필요한 정보만 반환
        return friendsRepository.findByUser(user).stream()
                .map(friendship -> {
                    User friend = friendship.getFriend();
                    GameCharacter character = friend.getUsers().isEmpty() ? null : friend.getUsers().get(0);

                    return new FriendDto(
                            friend.getLoginId(),
                            character != null ? character.getKindOfCharacter() : 0, // GameCharacter가 있을 때만 값 사용, 없으면 기본값
                            character != null ? character.getSpecies() : 0,
                            character != null ? character.getEvolutionLevel() : 0
                    );
                })
                .collect(Collectors.toList());
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