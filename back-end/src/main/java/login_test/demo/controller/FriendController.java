package login_test.demo.controller;

import login_test.demo.dto.FriendDto;
import login_test.demo.dto.SessionDto;
import login_test.demo.model.Friendship;
import login_test.demo.service.FriendService;
import login_test.demo.service.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        friendService.addFriend(loginId, friendId);

        return ResponseEntity.ok("loginId: " + loginId + " friendId: " + friendId + " 친구 추가 성공");
    }

    // 친구 목록 조회
    @GetMapping("/{loginId}")
    public ResponseEntity<List<FriendDto>> getFriends(@PathVariable("loginId") String loginId) {
        List<FriendDto> friends = friendService.getFriends(loginId);
        return ResponseEntity.ok(friends);
    }

    // 친구 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFriend(@RequestBody SessionDto sessionDto, @RequestParam("friendId") String friendId) {
        String loginId = redisUtil.getData(sessionDto.getSessionId());
        friendService.deleteFriend(loginId, friendId);
        return ResponseEntity.ok("Friend deleted successfully");
    }

}