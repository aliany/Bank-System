package com.ironhack.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ironhack.bank_system.convert.MoneyConverter;
import com.ironhack.bank_system.enums.TransferType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.javamoney.moneta.Money;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Data
@SuperBuilder
public class TransferDTO {

    private Long id;
    private String accountFromName;
    private AccountDTO accountFrom;
    private AccountDTO accountTo;
    private String concept;
    private String amountFormatted;
    @Builder.Default
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Digits(integer = 19, fraction = 4)
    private Double amountQty = Double.valueOf(0);
    @Builder.Default
    private Date date = new Date();
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private TransferType type = TransferType.TRANSFER;
    private Date createdDate;

    @NotNull
    private String ibanFrom;
    @NotNull
    private String ibanTo;
    @NotNull
    private String accountToName;

    public String getAccountFromName() {
        if (accountFrom != null) {
            return accountFrom.getPrimaryOwner().getName();
        }
        return null;
    }

}
