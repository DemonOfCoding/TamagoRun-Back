package login_test.demo.repository;

import login_test.demo.model.Coordinate;
import login_test.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface CoordinateRepository extends JpaRepository<Coordinate, Long> {
    List<Coordinate> findAllByUserIdAndCreatedDateBetween(Long user_id, Timestamp startOfDay, Timestamp endOfDay);
}