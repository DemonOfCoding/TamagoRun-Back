package login_test.demo.service;

import login_test.demo.dto.GameCharacterDto;
import login_test.demo.model.GameCharacter;
import login_test.demo.model.User;
import login_test.demo.repository.GameCharacterRepository;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class GameCharacterService {

    private final GameCharacterRepository gameCharacterRepository;
    private final UserRepository userRepository;

    // 게임 캐릭터 생성
    public void generateCharacter(GameCharacterDto gameCharacterDto) {

        if (gameCharacterDto.getUserId() == null) {
            throw new IllegalArgumentException("Character ID must not be null");
        }

        Random random = new Random();
        int randNum = random.nextInt(3) + 1;

        try {
            User user = userRepository.findById(gameCharacterDto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

            GameCharacter gameCharacter = GameCharacter.builder()
                    .user(user)
                    .evolutionLevel(0)
                    .experience(0)
                    .species(gameCharacterDto.getSpecies())
                    .kindOfCharacter(0)
                    .build();

            gameCharacterRepository.save(gameCharacter);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("게임 캐릭터 생성 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 종족 선택
    public void selectSpecies(Long userId, int species) {
        GameCharacter gameCharacter = gameCharacterRepository.findByUserId(userId);

        gameCharacter.setSpecies(species);
        gameCharacterRepository.save(gameCharacter);
    }

    // 캐릭터 종류 선택
    public int selectCharacter(Long userId) {
        GameCharacter gameCharacter = gameCharacterRepository.findByUserId(userId);
        Random randomValue = new Random();
        int characterType = randomValue.nextInt(4);

        gameCharacter.setKindOfCharacter(characterType);
        gameCharacterRepository.save(gameCharacter);

        return characterType;
    }
}