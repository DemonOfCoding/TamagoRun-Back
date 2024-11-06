package login_test.demo.service;

import login_test.demo.dto.AchievementDto;
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
    public AchievementDto evaluationAchievement(Long userId) {
        Achievement achievement = achievementRepository.getReferenceById(userId);
        User user = userRepository.getReferenceById(userId);

        // 총 러닝 횟수
        int runningCount = user.getTotalRunningCount();
        // 총 러닝 거리
        double runningDistance = user.getTotalRunningDistance();
        // 친구 수 가져오기
        int friendCount = friendsRepository.findByUser(user).size();

        // 첫 번째 러닝을 완료
        if (runningCount >= 1 && !achievement.isAchievementStatus1()) {
            achievement.setAchievementStatus1(true);
        }

        // 50번째 러닝 완료
        if (runningCount >= 50 && !achievement.isAchievementStatus2()) {
            achievement.setAchievementStatus2(true);
        }

        // 100번째 러닝 완료
        if (runningCount >= 100 && !achievement.isAchievementStatus3()) {
            achievement.setAchievementStatus3(true);
        }

        // 누적 42.195km 달성
        if (runningDistance >= 42.195 && !achievement.isAchievementStatus4()) {
            achievement.setAchievementStatus4(true);
        }

        // 누적 100km 달성
        if (runningDistance >= 100 && !achievement.isAchievementStatus5()) {
            achievement.setAchievementStatus5(true);
        }

        // 친구 맺기 10명 달성
        if (friendCount >= 10 && !achievement.isAchievementStatus6()) {
            achievement.setAchievementStatus6(true);
        }

        // 친구 맺기 20명 달성
        if (friendCount >= 20 && !achievement.isAchievementStatus7()) {
            achievement.setAchievementStatus7(true);
        }

        // 친구 맺기 30명 달성
        if (friendCount >= 30 && !achievement.isAchievementStatus8()) {
            achievement.setAchievementStatus8(true);
        }

        achievementRepository.save(achievement);

        return new AchievementDto(
                achievement.isAchievementStatus1(),
                achievement.isAchievementStatus2(),
                achievement.isAchievementStatus3(),
                achievement.isAchievementStatus4(),
                achievement.isAchievementStatus5(),
                achievement.isAchievementStatus6(),
                achievement.isAchievementStatus7(),
                achievement.isAchievementStatus8(),
                achievement.isFlag1(),
                achievement.isFlag2(),
                achievement.isFlag3(),
                achievement.isFlag4(),
                achievement.isFlag5(),
                achievement.isFlag6(),
                achievement.isFlag7(),
                achievement.isFlag8()
        );
    }

    // 업적 클리어 보상 획득
    public AchievementDto achievementReward(Long userId) {
        Achievement achievement = achievementRepository.findByUserId(userId);
        List<GameCharacter> gameCharacters = gameCharacterRepository.findAllByUserId(userId);

        // 활성 캐릭터 탐색
        GameCharacter activeCharacter = findActiveCharacter(gameCharacters);

        if (activeCharacter == null) {
            return null; // 레벨이 4인 캐릭터만 존재하면 종료
        }

        int gainExp = 0;

        if (achievement.isAchievementStatus1() && !achievement.isFlag1()) {
            gainExp += 1000;
            achievement.setFlag1(true);
        }

        if (achievement.isAchievementStatus2() && !achievement.isFlag2()) {
            gainExp += 25000;
            achievement.setFlag2(true);
        }

        if (achievement.isAchievementStatus3() && !achievement.isFlag3()) {
            gainExp += 50000;
            achievement.setFlag3(true);
        }

        if (achievement.isAchievementStatus4() && !achievement.isFlag4()) {
            gainExp += 10000;
            achievement.setFlag4(true);
        }

        if (achievement.isAchievementStatus5() && !achievement.isFlag5()) {
            gainExp += 18000;
            achievement.setFlag5(true);
        }

        if (achievement.isAchievementStatus6() && !achievement.isFlag6()) {
            gainExp += 5000;
            achievement.setFlag6(true);
        }

        if (achievement.isAchievementStatus7() && !achievement.isFlag7()) {
            gainExp += 10000;
            achievement.setFlag7(true);
        }

        if (achievement.isAchievementStatus8() && !achievement.isFlag8()) {
            gainExp += 15000;
            achievement.setFlag8(true);
        }

        addExperience(activeCharacter, gainExp);

        return new AchievementDto(
                achievement.isAchievementStatus1(),
                achievement.isAchievementStatus2(),
                achievement.isAchievementStatus3(),
                achievement.isAchievementStatus4(),
                achievement.isAchievementStatus5(),
                achievement.isAchievementStatus6(),
                achievement.isAchievementStatus7(),
                achievement.isAchievementStatus8(),
                achievement.isFlag1(),
                achievement.isFlag2(),
                achievement.isFlag3(),
                achievement.isFlag4(),
                achievement.isFlag5(),
                achievement.isFlag6(),
                achievement.isFlag7(),
                achievement.isFlag8()
        );
    }

    // 활성 캐릭터 찾기
    private GameCharacter findActiveCharacter(List<GameCharacter> gameCharacters) {
        for (GameCharacter character : gameCharacters) {
            if (character.getEvolutionLevel() < 4) {
                return character; // setEvolutionLevel();이 4 미만인 캐릭터 반환
            }
        }
        return null; // 모든 캐릭터가 레벨 4 이상이면 null 반환
    }

    // 캐릭터 경험치 추가 및 진화 조건 처리
    private void addExperience(GameCharacter character, int gainedExp) {
        int currentExp = character.getExperience();
        character.setExperience(currentExp + gainedExp);

        gameCharacterRepository.save(character); // 캐릭터 저장
    }
}