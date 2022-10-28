package com.yasselazha.suivifinancier.auth.repository;

import com.yasselazha.suivifinancier.auth.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

}
