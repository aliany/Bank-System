package com.ironhack.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.bank_system.enums.Status;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.javamoney.moneta.Money;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class CheckingDTO extends AccountDTO {

    private String secretKey;
    private String minimumBalanceFormatted;
    private String monthlyMaintenanceFeeFormatted;
    @Digits(integer = 19, fraction = 4)
    @Builder.Default
    private Double minimumBalanceQty = Double.valueOf(250);
    @Digits(integer = 19, fraction = 4)
    @Builder.Default
    private Double monthlyMaintenanceFeeQty = Double.valueOf(12);
    @Builder.Default
    private Date creationDate = new Date();
}
