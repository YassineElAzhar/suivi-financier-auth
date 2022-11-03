package com.yasselazhar.suivifinancier.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yasselazhar.suivifinancier.auth.model.Password;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Integer> {
	Password findByUserId(String userId);

}
