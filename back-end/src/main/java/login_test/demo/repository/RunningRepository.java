package login_test.demo.repository;

import login_test.demo.model.Running;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RunningRepository extends JpaRepository<Running, Long> {
    Running findByUserId(Long userId);
    List<Running> findAllByCreatedDateBefore(Timestamp timestamp);
}
