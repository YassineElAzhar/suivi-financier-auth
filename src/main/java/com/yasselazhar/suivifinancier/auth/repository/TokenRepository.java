package com.yasselazhar.suivifinancier.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yasselazhar.suivifinancier.auth.model.Token;


@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
	Token findByUserId(String userId);
	Token findByTokenContextAndUserId(String tokenContext, String userId);
}
