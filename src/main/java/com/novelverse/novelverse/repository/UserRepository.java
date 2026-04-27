package com.novelverse.novelverse.repository;

import com.novelverse.novelverse.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long username);
    Optional<User> findByUsername(String username);
    void save(User user);
    List<User> findAll();
}
