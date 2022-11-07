package com.yasselazhar.suivifinancier.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yasselazhar.suivifinancier.auth.model.SecureQuestion;

@Repository
public interface SecureQuestionRepository extends JpaRepository<SecureQuestion, Integer> {
	List<SecureQuestion> findByUserId(int userId);

}
