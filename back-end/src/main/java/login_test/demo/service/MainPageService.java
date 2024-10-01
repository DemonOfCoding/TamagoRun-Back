package login_test.demo.service;

import login_test.demo.dto.MainPageDto;
import login_test.demo.model.GameCharacter;
import login_test.demo.model.User;
import login_test.demo.repository.GameCharacterRepository;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final GameCharacterRepository gameCharacterRepository;
    private final UserRepository userRepository;

    public MainPageDto checkCharacter(String loginId) {

        User user = userRepository.findByLoginId(loginId);
        GameCharacter gameCharacter = gameCharacterRepository.findByUserId(user.getId());

        MainPageDto mainPageDto = MainPageDto.builder()
                .loginId(user.getLoginId())
                .experience(gameCharacter.getExperience())
                .kindOfCharacter(gameCharacter.getKindOfCharacter())
                .species(gameCharacter.getSpecies())
                .evolutionLevel(gameCharacter.getEvolutionLevel())
                .build();

        return mainPageDto;
    }
}
