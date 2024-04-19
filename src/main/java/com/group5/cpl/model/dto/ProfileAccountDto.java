package com.group5.cpl.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.sql.Date;
@Data
public class ProfileAccountDto {

    private Long id;
    private Date dob;
    @NotBlank(message = "Fullname can not blank")
    private String fullname;
    //@NotBlank(message = "Username can not blank")
    //private String username;
    @Pattern(regexp = "0\\d{9}", message = "Phone number must be 10 digits")
    private String phoneNumber;
    @NotBlank(message = "Address can not blank")
    private String address;
    @NotBlank(message = "Gender can not blank")
    private String gender;
    private String image;
}
