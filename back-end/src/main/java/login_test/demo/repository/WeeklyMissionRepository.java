package login_test.demo.repository;

import login_test.demo.model.WeeklyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyMissionRepository extends JpaRepository<WeeklyMission, Long> {
    List<WeeklyMission> findByUserId(Long userId);
}
