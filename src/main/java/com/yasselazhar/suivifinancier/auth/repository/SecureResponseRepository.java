package com.yasselazhar.suivifinancier.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yasselazhar.suivifinancier.auth.model.SecureResponse;

@Repository
public interface SecureResponseRepository extends JpaRepository<SecureResponse, Integer> {
	List<SecureResponse> findByUserId(int userId);
	List<SecureResponse> findBySecureQuestionId(int secureQuestionId);
	SecureResponse findBySecureQuestionIdAndUserId(int secureQuestionId, int userId);
}
