package com.sideproject.myshop.auth.repositories;

import com.sideproject.myshop.auth.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Authority findByRoleCode(String roleCode);

    boolean existsByRoleCode(String role);
}
