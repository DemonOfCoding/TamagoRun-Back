package login_test.demo.controller;

import login_test.demo.model.Friendship;
import login_test.demo.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    // 친구 추가
    @PostMapping("/add")
    public ResponseEntity<String> addFriend(@RequestParam("loginId") String loginId, @RequestParam("friendId") String friendId) {

        friendService.addFriend(loginId, friendId);
        return ResponseEntity.ok("loginId: " + loginId + " friendId: " + friendId + "친구 추가 성공");
    }

    // 친구 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<Friendship>> getFriends(@PathVariable("userId") Long userId) {
        List<Friendship> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    // 친구 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFriend(@RequestParam("loginId") String loginId, @RequestParam("friendId") String friendId) {
        friendService.deleteFriend(loginId, friendId);
        return ResponseEntity.ok("Friend deleted successfully");
    }

}
