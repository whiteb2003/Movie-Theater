package com.group5.cpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.group5.cpl.model.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity,Long>{
    @Query("SELECT u FROM RoleEntity u WHERE u.name = ?1")
    RoleEntity findByName(String name);
}
