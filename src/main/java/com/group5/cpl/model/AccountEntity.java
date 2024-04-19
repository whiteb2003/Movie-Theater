package com.group5.cpl.model;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "account")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Account_ID")
    private Long account_id;
    @Column(name = "Email", unique = true, length = 60)
    private String email;
    @Column(name = "Password", unique = false, length = 500)
    private String password;
    @Column(name = "User_Name", unique = true, length = 60)
    private String username;
    @Column(name = "Phone_Number", unique = true, length = 11)
    private String phoneNumber;
    @Column(name = "Full_Name", unique = false, length = 60)
    private String fullname;
    @Column(name = "Dob", unique = false)
    private Date dob;
    @Column(name = "Address", unique = false, length = 100)
    private String address;
    @Column(name = "Gender", unique = false, length = 10)
    private String gender;
    @Column(name = "Register_Date", unique = false)
    private LocalDate registerDate;
    @Column(name = "image", unique = false, length = 1000)
    private String image;
    @Column(name = "Score", unique = false)
    private double score;
    @Column(name = "Status", unique = false)
    private boolean status;
    @Column(name = "Verified_Code", unique = false, length = 500)
    private String verifyCode;
    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private RoleEntity roleEntity;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate localDate) {
        this.registerDate = localDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public RoleEntity getRoleEntity() {
        return roleEntity;
    }

    public void setRoleEntity(RoleEntity roleEntity) {
        this.roleEntity = roleEntity;
    }

    @Transient
    public String getUserImagePath() {
        if (image == null || account_id == null)
            return null;

        return "/images/" + image;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }

}
