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
    public ResponseEntity<EvolutionDto> selectSpecies(@RequestBody GameCharacterDto gameCharacterDto) {
        String loginId = redisUtil.getData(gameCharacterDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (loginId == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 사용자 없음
        }

        EvolutionDto evolutionDto = gameCharacterService.selectSpecies(user.getId(), gameCharacterDto.getSpecies());

        return ResponseEntity.ok(evolutionDto);
    }

    // 종류 선택
    @PostMapping("/selectCharacter")
    public ResponseEntity<EvolutionDto> selectCharacter(@RequestBody GameCharacterDto gameCharacterDto) {
        String loginId = redisUtil.getData(gameCharacterDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (loginId == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 사용자 없음
        }

        EvolutionDto evolutionDto = gameCharacterService.selectCharacter(user.getId());

        return ResponseEntity.ok(evolutionDto);
    }

    // 캐릭터 진화
    @PostMapping("/evolutionCharacter")
    public ResponseEntity<EvolutionDto> evolutionCharacter(@RequestBody GameCharacterDto gameCharacterDto) {
        String loginId = redisUtil.getData(gameCharacterDto.getSessionId());
        User user = userRepository.findByLoginId(loginId);

        if (loginId == null || user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 사용자 없음
        }

        EvolutionDto evolutionDto = gameCharacterService.evolutionCharacter(user.getId());

        return ResponseEntity.ok(evolutionDto);
    }
}