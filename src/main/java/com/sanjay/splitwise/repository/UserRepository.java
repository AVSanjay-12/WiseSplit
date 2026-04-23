package com.sanjay.splitwise.repository;

import com.sanjay.splitwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
