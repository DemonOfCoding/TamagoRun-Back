package login_test.demo.controller;

import login_test.demo.dto.GameCharacterDto;
import login_test.demo.service.GameCharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/character")
public class GameCharacterController {

    private final GameCharacterService gameCharacterService;

    // 게임 캐릭터 생성
    @PostMapping("/generation")
    public ResponseEntity<String> generateChar(@RequestBody GameCharacterDto gameCharacterDto) {
        try {
            gameCharacterService.generateCharacter(gameCharacterDto);
            return ResponseEntity.ok("게임 캐릭터가 성공적으로 생성되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 정보를 입력하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }

    }
}
