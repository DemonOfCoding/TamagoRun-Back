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

        // 첫 번째 친구 관계 생성 (user -> friend)
        Friendship friendship1 = new Friendship();
        friendship1.setUser(user);
        friendship1.setFriend(friend);

        // 두 번째 친구 관계 생성 (friend -> user)
        Friendship friendship2 = new Friendship();
        friendship2.setUser(friend);
        friendship2.setFriend(user);

        // 저장
        friendsRepository.save(friendship1);
        friendsRepository.save(friendship2);
    }

    // 친구 목록 조회
    public List<FriendDto> getFriends(String loginId) {
        User user = userRepository.findByLoginId(loginId);
        //List<Friendship> friendships = friendsRepository.findByUser(user);

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
    public boolean deleteFriend(String sessionId, String friendId) {
        // Redis에서 sessionId로 loginId 조회
        if (sessionId == null) {
            throw new IllegalArgumentException("Invalid session ID");
        }

        // loginId로 User 객체 조회
        User user = userRepository.findByLoginId(sessionId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for the given session ID");
        }
        System.out.println("User ID: " + user.getId());

        // friendId로 친구 User 객체 조회
        User friend = userRepository.findByLoginId(friendId);
        if (friend == null) {
            throw new IllegalArgumentException("Friend not found with login ID: " + friendId);
        }
        System.out.println("Friend ID: " + friend.getId());

        // 친구 관계가 존재하는지 확인
        if (!friendsRepository.existsByUserAndFriend(user, friend)) {
            System.out.println("Friendship does not exist");
            return false;
        }

        // 친구 관계 삭제
        friendsRepository.deleteByUserAndFriend(user, friend);
        friendsRepository.deleteByUserAndFriend(friend, user);
        return true; // 삭제 성공 시 true 반환
    }
}