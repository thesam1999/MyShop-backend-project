package com.sideproject.myshop.auth.repositories;

import com.sideproject.myshop.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserDetailRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
}
