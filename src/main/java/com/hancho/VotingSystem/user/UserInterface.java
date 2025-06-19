package com.hancho.VotingSystem.user;

import com.hancho.VotingSystem.user.dtos.UserRecord;
import java.util.Optional;

public interface UserInterface {

  public Optional<Users> findUser(String email);

  public Users createUser(UserRecord user);
}
