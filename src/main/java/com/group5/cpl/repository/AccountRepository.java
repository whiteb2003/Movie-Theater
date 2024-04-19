package com.group5.cpl.repository;

import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.group5.cpl.model.AccountEntity;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
        @Query("SELECT u FROM AccountEntity u WHERE u.email = ?1")
        public Optional<AccountEntity> findUserByEmail(String email);

        @Query("SELECT u FROM AccountEntity u WHERE u.username = ?1")
        public Optional<AccountEntity> findUserByUsernName1(String username);

        @Query("SELECT u FROM AccountEntity u WHERE u.phoneNumber = ?1")
        public Optional<AccountEntity> findUserByPhone(String phoneNumber);

        @Query("SELECT u FROM AccountEntity u WHERE u.username = ?1")
        public AccountEntity getUserByUserName(String username);

        @Query("SELECT u FROM AccountEntity u WHERE u.verifyCode = ?1")
        public AccountEntity findByVerificationCode(String code);

        @Query("SELECT u FROM AccountEntity u WHERE u.account_id = ?1")
        public AccountEntity findUserById(Long id);

        @Transactional
        @Modifying
        @Query("UPDATE AccountEntity u SET u.status = true, u.verifyCode = null WHERE u.account_id = ?1")
        public void enable(Long id);

        @Transactional
        @Modifying
        @Query("UPDATE AccountEntity u SET u.status = ?1, u.verifyCode = null WHERE u.account_id = ?2")
        public void setActive(boolean check, Long id);

        public List<AccountEntity> findByRoleEntity_Id(Long role_id);

        @Query("SELECT u FROM AccountEntity u WHERE u.roleEntity.id = 3")
        public List<AccountEntity> listAllExceptEmployee();

        @Transactional
        @Modifying
        @Query("UPDATE AccountEntity u SET u.roleEntity.id = 1 WHERE u.account_id = ?1")
        public void removeEmployee(Long id);

        @Transactional
        @Modifying
        @Query("UPDATE AccountEntity u SET u.roleEntity.id = 3 WHERE u.account_id = ?1")
        public void addEmployee(Long id);

        @Query("SELECT u FROM AccountEntity u WHERE u.email = :keyword ")
        public AccountEntity searchForAddEmployee(String keyword);

        @Query("SELECT u FROM AccountEntity u WHERE" +
                        "(:email IS NULL OR :email = '' OR u.email = :email)" +
                        "AND (:username IS NULL OR :username = '' OR u.username = :username)" +
                        "AND (:phoneNumber IS NULL OR :phoneNumber = '' OR u.phoneNumber = :phoneNumber)" +
                        "AND u.roleEntity.id = 3")
        List<AccountEntity> findEmployee(@Param("username") String username,
                        @Param("email") String email,
                        @Param("phoneNumber") String phoneNumber);

        @Query("SELECT u FROM AccountEntity u WHERE u.roleEntity.name = 'MANAGER' ")
        public AccountEntity listOneAdmin();
}
