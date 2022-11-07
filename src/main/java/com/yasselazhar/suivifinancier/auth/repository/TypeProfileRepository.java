package com.yasselazhar.suivifinancier.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yasselazhar.suivifinancier.auth.model.TypeProfile;

@Repository
public interface TypeProfileRepository extends JpaRepository<TypeProfile, Integer> {

}
