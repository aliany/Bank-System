package com.ironhack.bank_system.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class CreditCardDTO extends AccountDTO {

    private String creditLimitFormatted;
    @Digits(integer = 19, fraction = 4)
    @DecimalMin(value = "100", message = "maximo es 100")
    @DecimalMax(value = "100000", message = "maximo es 100000")
    @Builder.Default
    private Double creditLimitQty = Double.valueOf(100);
    @DecimalMin(value = "0.1")
    @Digits(integer = 19, fraction = 4)
    @Builder.Default
    private BigDecimal interestRate = BigDecimal.valueOf(0.2);

}
