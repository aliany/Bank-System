package com.ironhack.bank_system.model;

import com.ironhack.bank_system.convert.MoneyConverter;
import com.ironhack.bank_system.utils.Utils;
import lombok.*;
import lombok.experimental.SuperBuilder;
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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration
@EqualsAndHashCode(callSuper = false)
@PrimaryKeyJoinColumn
@SuperBuilder
@DiscriminatorValue("CREDITCARD")
public class CreditCard extends Account {

    @Builder.Default
    @NotNull
    @DecimalMin(value = "100", message = "maximo es 100")
    @DecimalMax(value = "100000", message = "maximo es 100000")
    @Digits(integer = 19, fraction = 4)
    @Convert(converter = MoneyConverter.class)
    private Money creditLimit = Money.of(100, Monetary.getCurrency("EUR"));
    @Builder.Default
    @Digits(integer = 19, fraction = 4)
    @NotNull
    @DecimalMin(value = "0.1")
    @Digits(integer = 19, fraction = 4)
    private BigDecimal interestRate = BigDecimal.valueOf(0.2);

    @PrePersist
    private void beforeCreate() throws InvalidKeySpecException, NoSuchAlgorithmException {
        setSecretKey(Utils.generateSecretKey());
        setIban(
                new Iban.Builder()
                        .countryCode(CountryCode.ES)
                        .bankCode("0049")
                        .buildRandom().toString()
        );
    }

}
