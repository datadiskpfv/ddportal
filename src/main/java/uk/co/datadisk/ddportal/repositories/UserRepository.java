package uk.co.datadisk.ddportal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.datadisk.ddportal.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
