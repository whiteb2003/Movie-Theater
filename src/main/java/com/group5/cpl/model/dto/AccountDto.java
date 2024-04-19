package com.group5.cpl.model.dto;

import java.sql.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    @NotBlank(message = "Email cannot blank")
    @Email(message = "Invalid email")
    private String email;
    private String password;
    private String confirmPassword;
    private Date dob;
    @NotBlank(message = "Fullname can not blank")
    private String fullname;
    @NotBlank(message = "Username can not blank")
    private String username;
    @Pattern(regexp = "0\\d{9}", message = "Phone number must be 10 digits")
    private String phoneNumber;
    private String verify_Code;
    @NotBlank(message = "Address can not blank")
    private String address;
    @NotBlank(message = "Gender can not blank")
    private String gender;
}
