package com.hancho.VotingSystem.user;

import com.hancho.VotingSystem.user.dtos.UserRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
  Optional<Users> findByEmail(String email);

  Users save(UserRecord user);
}
