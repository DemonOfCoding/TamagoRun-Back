package login_test.demo.controller;

import login_test.demo.dto.FriendDto;
import login_test.demo.dto.SessionDto;
import login_test.demo.model.Friendship;
import login_test.demo.service.FriendService;
import login_test.demo.service.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;
    private final RedisUtil redisUtil;

    // 친구 추가
    @PostMapping("/add")
    public ResponseEntity<String> addFriend(@RequestBody SessionDto sessionDto, @RequestParam("friendId") String friendId) {

        String loginId = redisUtil.getData(sessionDto.getSessionId());
        System.out.println("loginId: " + loginId + " friendId: " + friendId);

        // 자신과 친구를 맺을 수 없도록 조건 추가
        if (loginId.equals(friendId)) {
            return ResponseEntity.status(400).body("자기 자신과는 친구를 맺을 수 없습니다.");
        }

        friendService.addFriend(loginId, friendId);

        return ResponseEntity.ok("loginId: " + loginId + " friendId: " + friendId + " 친구 추가 성공");
    }

    // 친구 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> getFriends(@RequestBody SessionDto sessionDto) {
        String loginId = redisUtil.getData(sessionDto.getSessionId()); // Redis에서 sessionId로 loginId 조회

        if (loginId == null) {
            return ResponseEntity.status(401).body(null); // 세션이 유효하지 않을 경우
        }

        List<FriendDto> friends = friendService.getFriends(loginId);
        if (friends.isEmpty()) {
            return ResponseEntity.ok("친구 목록이 비어있습니다."); // 빈 리스트 반환
        }

        return ResponseEntity.ok(friends);
    }

    // 친구 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFriend(@RequestBody SessionDto sessionDto, @RequestParam("friendId") String friendId) {
        String loginId = redisUtil.getData(sessionDto.getSessionId());
        boolean isDeleted = friendService.deleteFriend(loginId, friendId); // 삭제 결과 확인

        if (isDeleted) {
            return ResponseEntity.ok("Friend deleted successfully");
        } else {
            return ResponseEntity.status(404).body("친구 삭제 실패, 친구 관계가 존재하지 않습니다.");
        }
    }

}