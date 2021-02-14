package com.ironhack.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.bank_system.enums.Rol;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Data
public class UserDTO {
    private Long id;
    @NotNull(message = "The username cannot be null")
    private String username;
    @NotNull(message = "The password cannot be null")
    private String password;
    @NotNull(message = "The role cannot be null")
    private String rol;
}
