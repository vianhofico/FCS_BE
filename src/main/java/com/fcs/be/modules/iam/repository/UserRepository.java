package com.fcs.be.modules.iam.repository;

import com.fcs.be.modules.iam.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByIdAndIsDeletedFalse(UUID id);

    boolean existsByUsernameAndIsDeletedFalse(String username);

    boolean existsByEmailAndIsDeletedFalse(String email);

    @Query("SELECT u FROM User u WHERE (u.username = :identifier OR u.email = :identifier) AND u.isDeleted = false")
    Optional<User> findByUsernameOrEmail(String identifier);

    List<User> findByIsDeletedFalseOrderByCreatedAtDesc();
}
