package com.ironhack.bank_system.model;

import com.ironhack.bank_system.convert.MoneyConverter;
import com.ironhack.bank_system.utils.Utils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.javamoney.moneta.Money;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.money.Monetary;
import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Objects;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration
@EqualsAndHashCode(callSuper = false)
@PrimaryKeyJoinColumn
@SuperBuilder
@DiscriminatorValue("SAVING")
public class Saving extends Account {

    @Builder.Default
    @NotNull
    @DecimalMin(value = "100", message = "minimo es 100")
    @Digits(integer = 19, fraction = 4)
    @Convert(converter = MoneyConverter.class)
    private Money minimumBalance = Money.of(1000, Monetary.getCurrency("EUR"));
    @Builder.Default
    @NotNull
    @Digits(integer = 19, fraction = 4)
    @DecimalMax(value = "0.5", message = "maximo es 0.5")
    private BigDecimal interestRate = BigDecimal.valueOf(0.0025);

    @PrePersist
    private void beforeCreate() throws NoSuchAlgorithmException, InvalidKeySpecException {
        setSecretKey(Utils.generateSecretKey());
        calculateBalance();
        setIban(
                new Iban.Builder()
                        .countryCode(CountryCode.ES)
                        .bankCode("0049")
                        .buildRandom().toString()
        );
    }

    @PreUpdate
    private void beforeUpdate() {
        calculateBalance();
    }

    private void calculateBalance() {
        if (getBalance().isLessThan(getMinimumBalance()) && Objects.isNull(getPenaltyFeeApplied())) {
            setBalance(getBalance().subtract(getPenaltyFee()));
            setPenaltyFeeApplied(new Date());
        } else if (getBalance().isGreaterThanOrEqualTo(getMinimumBalance())) {
            setPenaltyFeeApplied(null);
        }
    }

}