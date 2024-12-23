package com.I4U.mukpic.repository;

import com.I4U.mukpic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email); //email 중복 검사
    boolean existsByUserId(String userId); //아이디 중복 검사
}
