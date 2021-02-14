package com.ironhack.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Data
@SuperBuilder
public class ThirdPartyTransferDTO {

    @NotNull
    private Long accountToId;
    @NotNull
    private String accountFromSecretKey;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Digits(integer = 19, fraction = 4)
    private Double amountQty = Double.valueOf(0);

}
