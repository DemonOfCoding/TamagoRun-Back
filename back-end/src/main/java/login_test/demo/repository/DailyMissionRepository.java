package login_test.demo.repository;

import login_test.demo.model.DailyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {
    DailyMission findByUserId(Long userId);
}
