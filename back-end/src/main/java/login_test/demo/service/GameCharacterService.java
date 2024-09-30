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
                    .kindOfCharacter(randNum)
                    .build();

            gameCharacterRepository.save(gameCharacter);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("게임 캐릭터 생성 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 오류가 발생했습니다: " + e.getMessage());
        }
    }
}