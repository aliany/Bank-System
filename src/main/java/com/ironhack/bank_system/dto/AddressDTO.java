package com.ironhack.bank_system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Data
public class AddressDTO {
    private Long id;
    @NotNull(message = "The address cannot be null")
    private String address;
}
