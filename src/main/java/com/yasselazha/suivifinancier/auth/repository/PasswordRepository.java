package com.yasselazha.suivifinancier.auth.repository;

import com.yasselazha.suivifinancier.auth.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PasswordRepository extends JpaRepository<Password, Integer> {

}
