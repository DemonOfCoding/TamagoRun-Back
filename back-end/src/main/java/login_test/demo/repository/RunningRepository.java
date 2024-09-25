package login_test.demo.repository;

import login_test.demo.model.Running;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunningRepository extends JpaRepository<Running, Long> {

}
