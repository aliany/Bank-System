package com.ironhack.bank_system.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class SavingDTO extends AccountDTO {

    private String secretKey;
    private String minimumBalanceFormatted;
    @Digits(integer = 19, fraction = 4)
    @DecimalMin(value = "100", message = "minimo es 100")
    @Builder.Default
    private Double minimumBalanceQty = Double.valueOf(1000);
    @Digits(integer = 19, fraction = 4)
    @DecimalMax(value = "0.5", message = "maximo es 0.5")
    @Builder.Default
    private BigDecimal interestRate = BigDecimal.valueOf(0.0025);
}
