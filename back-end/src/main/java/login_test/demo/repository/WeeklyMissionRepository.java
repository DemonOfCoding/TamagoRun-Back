package login_test.demo.repository;

import login_test.demo.model.WeeklyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyMissionRepository extends JpaRepository<WeeklyMission, Long> {
    WeeklyMission findByUserId(Long userId);
}
