package com.yasselazha.suivifinancier.auth.repository;

import com.yasselazha.suivifinancier.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
