package com.yasselazhar.suivifinancier.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yasselazhar.suivifinancier.auth.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	User findByEmail(String email);
}
