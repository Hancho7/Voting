package com.hancho.VotingSystem.user;

import com.hancho.VotingSystem.user.dtos.UserRecord;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
class UserService implements UserInterface {
  private final UsersRepository repository;

  public UserService(UsersRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<Users> findUser(String email) {
    return repository.findByEmail(email);
  }

  @Override
  public Users createUser(UserRecord user) {

    return repository.save(user);
  }
}
