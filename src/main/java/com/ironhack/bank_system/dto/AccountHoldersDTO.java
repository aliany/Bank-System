package com.ironhack.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.bank_system.model.Address;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;


@NoArgsConstructor
@Data
public class AccountHoldersDTO {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private Date dateOfBirth;
    @NotNull
    private Long primaryAddressId;
    private AddressDTO primaryAddress;
    private Long mailingAddressId;
    private AddressDTO mailingAddress;
    @NotNull
    private Long userId;
    @JsonIgnore
    private UserDTO user;
}
