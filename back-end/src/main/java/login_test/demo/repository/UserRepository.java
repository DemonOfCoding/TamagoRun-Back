package login_test.demo.repository;

import login_test.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLoginId(String loginId);

    User findByLoginIdAndEmail(String loginId, String email);
}