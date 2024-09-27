package login_test.demo.service;

import login_test.demo.model.Achievement;
import login_test.demo.model.GameCharacter;
import login_test.demo.model.User;
import login_test.demo.repository.AchievementRepository;
import login_test.demo.repository.FriendsRepository;
import login_test.demo.repository.GameCharacterRepository;
import login_test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final GameCharacterRepository gameCharacterRepository;
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    // 업적 평가
    public void evaluationAchievement(Long userId) {
        Achievement achievement = achievementRepository.getReferenceById(userId);
        User user = userRepository.getReferenceById(userId);
        List<GameCharacter> gameCharacters = gameCharacterRepository.findAllByUserId(userId);

        // 활성 캐릭터 탐색
        GameCharacter activeCharacter = findActiveCharacter(gameCharacters);

        if (activeCharacter == null) {
            return; // 레벨이 4인 캐릭터만 존재하면 종료
        }

        int gainExp = 0;
        // 총 러닝 횟수
        int runningCount = user.getTotalRunningCount();
        // 총 러닝 거리
        double runningDistance = user.getTotalRunningDistance();
        // 친구 수 가져오기
        int friendCount = friendsRepository.findByUser(user).size();

        // 첫 번째 러닝을 완료
        if (runningCount >= 1 && !achievement.isAchievementStatus1()) {
            achievement.setAchievementStatus1(true);
            gainExp += 1000;
        }

        // 50번째 러닝 완료
        if (runningCount >= 50 && !achievement.isAchievementStatus2()) {
            achievement.setAchievementStatus2(true);
            gainExp += 25000;
        }

        // 100번째 러닝 완료
        if (runningCount >= 100 && !achievement.isAchievementStatus3()) {
            achievement.setAchievementStatus3(true);
            gainExp += 50000;
        }

        // 누적 42.195km 달성
        if (runningDistance >= 42.195 && !achievement.isAchievementStatus4()) {
            achievement.setAchievementStatus4(true);
            gainExp += 10000;
        }

        // 누적 100km 달성
        if (runningDistance >= 100 && !achievement.isAchievementStatus5()) {
            achievement.setAchievementStatus5(true);
            gainExp += 18000;
        }

        // 친구 맺기 10명 달성
        if (friendCount >= 10 && !achievement.isAchievementStatus6()) {
            achievement.setAchievementStatus6(true);
            gainExp += 5000;
        }

        // 친구 맺기 20명 달성
        if (friendCount >= 20 && !achievement.isAchievementStatus7()) {
            achievement.setAchievementStatus7(true);
            gainExp += 10000;
        }

        // 친구 맺기 30명 달성
        if (friendCount >= 30 && !achievement.isAchievementStatus8()) {
            achievement.setAchievementStatus8(true);
            gainExp += 15000;
        }

        addExperience(activeCharacter, gainExp);
        achievementRepository.save(achievement);
    }

    // 활성 캐릭터 찾기
    private GameCharacter findActiveCharacter(List<GameCharacter> gameCharacters) {
        for (GameCharacter character : gameCharacters) {
            if (character.getCharacterLevel() < 4) {
                return character; // 레벨이 4 미만인 캐릭터 반환
            }
        }
        return null; // 모든 캐릭터가 레벨 4 이상이면 null 반환
    }

    // 캐릭터 경험치 추가 및 진화 조건 처리
    private void addExperience(GameCharacter character, int gainedExp) {
        int currentExp = character.getExperience();
        character.setExperience(currentExp + gainedExp);

        // 진화 조건 체크 및 레벨업
        if (character.getCharacterLevel() == 0 && character.getExperience() >= 8000) {
            character.setCharacterLevel(1);
            character.setExperience(character.getExperience() - 8000); // 진화 후 남은 경험치
        } else if (character.getCharacterLevel() == 1 && character.getExperience() >= 15000) {
            character.setCharacterLevel(2);
            character.setExperience(character.getExperience() - 15000); // 진화 후 남은 경험치
        } else if (character.getCharacterLevel() == 2 && character.getExperience() >= 30000) {
            character.setCharacterLevel(3);
            character.setExperience(character.getExperience() - 30000); // 진화 후 남은 경험치
        } else if (character.getCharacterLevel() == 3 && character.getExperience() >= 50000) {
            character.setCharacterLevel(4);
        }

        gameCharacterRepository.save(character); // 캐릭터 저장
    }
}
