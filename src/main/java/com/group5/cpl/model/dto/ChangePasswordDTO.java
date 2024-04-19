package com.group5.cpl.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {
    private Long userId;
    @NotBlank(message="Password cannot blank")
    private String currentPassword;
    @Size(min=5,max = 20, message = "Password length must be between 5 and 20 characters")
    private String newPassword;
    @Size(min=5,max = 20, message = "Password length must be between 5 and 20 characters")
    private String rePassword;
}
