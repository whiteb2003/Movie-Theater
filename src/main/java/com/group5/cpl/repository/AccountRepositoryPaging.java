package com.group5.cpl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.group5.cpl.model.AccountEntity;

public interface AccountRepositoryPaging extends PagingAndSortingRepository<AccountEntity, Long> {
    @Query("SELECT a FROM AccountEntity a WHERE a.roleEntity.id!=2")
    public Page<AccountEntity> listAll(Pageable pageable);

    @Query("SELECT a FROM AccountEntity a WHERE a.roleEntity.id!=2 and a.username like %?1% and a.email like %?2% and a.phoneNumber like %?3%")
    public Page<AccountEntity> listAll(Pageable pageable, String username, String email, String phone);

    @Query("SELECT a FROM AccountEntity a WHERE a.status = true and a.roleEntity.id!=2")
    public Page<AccountEntity> listAllActive(Pageable pageable);

    @Query("SELECT a FROM AccountEntity a WHERE a.status = true and a.roleEntity.id!=2 and a.username like %?1% and a.email like %?2% and a.phoneNumber like %?3%")
    public Page<AccountEntity> listAllActive(Pageable pageable, String username, String email, String phone);

    @Query("SELECT a FROM AccountEntity a WHERE a.status = false and a.roleEntity.id!=2")
    public Page<AccountEntity> listAllBan(Pageable pageable);

    @Query("SELECT a FROM AccountEntity a WHERE a.status = false and a.roleEntity.id!=2 and a.username like %?1% and a.email like %?2% and a.phoneNumber like %?3%")
    public Page<AccountEntity> listAllBan(Pageable pageable, String username, String email, String phone);
}
