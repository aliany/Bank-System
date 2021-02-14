package com.ironhack.bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.bank_system.enums.Status;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.javamoney.moneta.Money;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@Data
@SuperBuilder
public class AccountDTO {

    private Long id;
    private String iban;
    private String balanceFormatted;
    private String penaltyFeeFormatted;
    @NotNull
    @Digits(integer = 19, fraction = 4)
    private Double balanceQty;
    @Builder.Default
    @Digits(integer = 19, fraction = 4)
    private Double penaltyFeeQty = Double.valueOf(40);
    @NotNull
    private Long primaryOwnerId;
    private AccountHoldersDTO primaryOwner;
    private Long secondaryOwnerId;
    private AccountHoldersDTO secondaryOwner;
    @Builder.Default
    private Status status = Status.ACTIVE;
    private Date createdDate;
    private Date updatedDate;
}
