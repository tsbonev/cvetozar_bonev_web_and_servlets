package com.clouway.bankapp.core;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> getById(int id);

    List<User> getAll();

    void deleteById(int id);

    void update(User user);

    Optional<User> getByUsername(String username);

    User registerIfNotExists(UserRegistrationRequest registerRequest) throws UserAlreadyExistsException;

}
