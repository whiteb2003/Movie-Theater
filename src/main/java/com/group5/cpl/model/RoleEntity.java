package com.group5.cpl.model;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
@Entity
@Table(name="role")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Role_ID")
    private Long id;
    @Column(name = "Role_Name", unique = true, length = 20)
    private String name;
    @OneToMany(mappedBy = "roleEntity", cascade = CascadeType.ALL)
    private List<AccountEntity> accounts;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<AccountEntity> getAccounts() {
        return accounts;
    }
    public void setAccounts(List<AccountEntity> accounts) {
        this.accounts = accounts;
    }
}
