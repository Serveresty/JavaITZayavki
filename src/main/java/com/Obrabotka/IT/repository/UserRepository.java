package com.Obrabotka.IT.repository;

import com.Obrabotka.IT.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
