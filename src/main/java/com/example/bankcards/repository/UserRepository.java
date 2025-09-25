package com.example.bankcards.repository;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByLogin(String login);

  @NativeQuery("SELECT EXISTS(SELECT id FROM users WHERE role='ADMIN'::role_type) AS record;")
  boolean hasAdmin();

  @Modifying
  @Query("UPDATE User u SET u.password = ?1 WHERE u.id = ?2")
  void updatePassword(String encode, long userId);

  @Modifying
  @Query(
      "UPDATE User u SET u.name = ?1, u.lastName = ?2, u.surname = ?3, u.login = ?4, u.role = ?5 WHERE u.id = ?6")
  void update(String name, String lastName, String surname, String login, Role role, Long id);

  @Modifying
  @Query("UPDATE User u SET u.status = ?2 WHERE u.id = ?1")
  void updateUserStatus(Long userId, UserStatus userStatus);
}
