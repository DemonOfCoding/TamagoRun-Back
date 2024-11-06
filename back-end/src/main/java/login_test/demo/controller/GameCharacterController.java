package login_test.demo.controller;

import login_test.demo.dto.EvolutionDto;
import login_test.demo.dto.GameCharacterDto;
import login_test.demo.dto.SessionDto;
import login_test.demo.model.GameCharacter;
import login_test.demo.model.User;
import login_test.demo.repository.GameCharacterRepository;
import login_test.demo.repository.UserRepository;
import login_test.demo.service.GameCharacterService;
import login_test.demo.service.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/character")
public class GameCharacterController {
    private final GameCharacterRepository gameCharacterRepository;
    private final GameCharacterService gameCharacterService;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    // 종족 선택
    @PostMapping("/selectSpecies")
    public ResponseEntity<String> selectSpecies(@RequestBody GameCharacterDto gameCharacterDto) {
        String loginId = redisUtil.getData(gameCharacterDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (loginId == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 사용자를 찾을 수 없습니다.");
        }

        gameCharacterService.selectSpecies(user.getId(), gameCharacterDto.getSpecies());

        return ResponseEntity.ok("종족 "+ gameCharacterDto.getSpecies() + "을 선택하셨습니다.");
    }

    // 종류 선택
    @PostMapping("/selectCharacter")
    public ResponseEntity<String> selectCharacter(@RequestBody GameCharacterDto gameCharacterDto) {
        String loginId = redisUtil.getData(gameCharacterDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);
        int characterType;

        if (loginId == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 사용자를 찾을 수 없습니다.");
        }

        characterType = gameCharacterService.selectCharacter(user.getId());

        return ResponseEntity.ok("캐릭터 " + characterType + "을 선택되었습니다.");
    }

    // 캐릭터 진화
    @PostMapping("/evolutionCharacter")
    public ResponseEntity<String> evolutionCharacter(@RequestBody GameCharacterDto gameCharacterDto) {
        String loginId = redisUtil.getData(gameCharacterDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);
        GameCharacter gameCharacter = gameCharacterRepository.findByUserId(user.getId());

        if (loginId == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 사용자를 찾을 수 없습니다.");
        }

        EvolutionDto evolutionDto = gameCharacterService.evolutionCharacter(user.getId());

        // 캐릭터가 최대 레벨일 경우
        if (gameCharacter.getEvolutionLevel() == 3)
            return ResponseEntity.ok("캐릭터가 최대 레벨입니다.\n" + evolutionDto);

        return ResponseEntity.ok("캐릭터가 진화하였습니다.\n" +evolutionDto);
    }
}