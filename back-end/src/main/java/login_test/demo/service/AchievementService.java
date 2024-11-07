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
        // 총 칼로리
        int calorie = user.getTotalCalorie();
        // 총 러닝 거리
        double runningDistance = user.getTotalRunningDistance();
        // 친구 수 가져오기
        int friendCount = friendsRepository.findByUser(user).size();

        // 첫 번째 러닝을 완료
        if (runningCount >= 1 && !achievement.isAchievementStatus1()) {
            achievement.setAchievementStatus1(true);
        }

        // 10번째 러닝 완료
        if (runningCount >= 10 && !achievement.isAchievementStatus2()) {
            achievement.setAchievementStatus2(true);
        }

        // 30번째 러닝 완료
        if (runningCount >= 30 && !achievement.isAchievementStatus3()) {
            achievement.setAchievementStatus3(true);
        }

        // 50번째 러닝 완료
        if (runningCount >= 30 && !achievement.isAchievementStatus4()) {
            achievement.setAchievementStatus4(true);
        }

        // 누적 42.195km 달성
        if (runningDistance >= 42.195 && !achievement.isAchievementStatus5()) {
            achievement.setAchievementStatus5(true);
        }

        // 누적 100km 달성
        if (runningDistance >= 100 && !achievement.isAchievementStatus6()) {
            achievement.setAchievementStatus6(true);
        }

        // 첫 친추 달성
        if (friendCount >= 1 && !achievement.isAchievementStatus7()) {
            achievement.setAchievementStatus7(true);
        }

        // 친구 맺기 5명 달성
        if (friendCount >= 5 && !achievement.isAchievementStatus8()) {
            achievement.setAchievementStatus8(true);
        }

        // 친구 맺기 10명 달성
        if (friendCount >= 10 && !achievement.isAchievementStatus9()) {
            achievement.setAchievementStatus9(true);
        }

        // 친구 맺기 15명 달성
        if (friendCount >= 15 && !achievement.isAchievementStatus10()) {
            achievement.setAchievementStatus10(true);
        }

        if (calorie >= 1000 && !achievement.isAchievementStatus11()) {
            achievement.setAchievementStatus11(true);
        }

        if (calorie >= 2000 && !achievement.isAchievementStatus12()) {
            achievement.setAchievementStatus12(true);
        }

        if (calorie >= 3000 && !achievement.isAchievementStatus13()) {
            achievement.setAchievementStatus13(true);
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
                achievement.isAchievementStatus9(),
                achievement.isAchievementStatus10(),
                achievement.isAchievementStatus11(),
                achievement.isAchievementStatus12(),
                achievement.isAchievementStatus13(),
                achievement.isFlag1(),
                achievement.isFlag2(),
                achievement.isFlag3(),
                achievement.isFlag4(),
                achievement.isFlag5(),
                achievement.isFlag6(),
                achievement.isFlag7(),
                achievement.isFlag8(),
                achievement.isFlag9(),
                achievement.isFlag10(),
                achievement.isFlag11(),
                achievement.isFlag12(),
                achievement.isFlag13()
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
            gainExp += 300;
            achievement.setFlag1(true);
        }

        if (achievement.isAchievementStatus2() && !achievement.isFlag2()) {
            gainExp += 1000;
            achievement.setFlag2(true);
        }

        if (achievement.isAchievementStatus3() && !achievement.isFlag3()) {
            gainExp += 1500;
            achievement.setFlag3(true);
        }

        if (achievement.isAchievementStatus4() && !achievement.isFlag4()) {
            gainExp += 2000;
            achievement.setFlag4(true);
        }

        if (achievement.isAchievementStatus5() && !achievement.isFlag5()) {
            gainExp += 1500;
            achievement.setFlag5(true);
        }

        if (achievement.isAchievementStatus6() && !achievement.isFlag6()) {
            gainExp += 3000;
            achievement.setFlag6(true);
        }

        if (achievement.isAchievementStatus7() && !achievement.isFlag7()) {
            gainExp += 300;
            achievement.setFlag7(true);
        }

        if (achievement.isAchievementStatus8() && !achievement.isFlag8()) {
            gainExp += 500;
            achievement.setFlag8(true);
        }

        if (achievement.isAchievementStatus9() && !achievement.isFlag9()) {
            gainExp += 1000;
            achievement.setFlag9(true);
        }

        if (achievement.isAchievementStatus10() && !achievement.isFlag10()) {
            gainExp += 1500;
            achievement.setFlag10(true);
        }

        if (achievement.isAchievementStatus11() && !achievement.isFlag11()) {
            gainExp += 500;
            achievement.setFlag11(true);
        }

        if (achievement.isAchievementStatus12() && !achievement.isFlag12()) {
            gainExp += 1000;
            achievement.setFlag12(true);
        }

        if (achievement.isAchievementStatus13() && !achievement.isFlag13()) {
            gainExp += 1500;
            achievement.setFlag13(true);
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
                achievement.isAchievementStatus9(),
                achievement.isAchievementStatus10(),
                achievement.isAchievementStatus11(),
                achievement.isAchievementStatus12(),
                achievement.isAchievementStatus13(),
                achievement.isFlag1(),
                achievement.isFlag2(),
                achievement.isFlag3(),
                achievement.isFlag4(),
                achievement.isFlag5(),
                achievement.isFlag6(),
                achievement.isFlag7(),
                achievement.isFlag8(),
                achievement.isFlag9(),
                achievement.isFlag10(),
                achievement.isFlag11(),
                achievement.isFlag12(),
                achievement.isFlag13()
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