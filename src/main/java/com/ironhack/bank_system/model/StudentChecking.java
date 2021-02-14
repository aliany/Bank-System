package com.ironhack.bank_system.model;

import com.ironhack.bank_system.enums.Status;
import com.ironhack.bank_system.enums.TransferType;
import com.ironhack.bank_system.utils.Utils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.javamoney.moneta.Money;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@EnableAutoConfiguration
@EqualsAndHashCode(callSuper = false)
@PrimaryKeyJoinColumn
@SuperBuilder
@DiscriminatorValue("STUDENTCHECKING")
public class StudentChecking extends Account {

    @PrePersist
    private void beforeCreate() throws NoSuchAlgorithmException, InvalidKeySpecException {
        setSecretKey(Utils.generateSecretKey());
        setIban(
                new Iban.Builder()
                        .countryCode(CountryCode.ES)
                        .bankCode("0049")
                        .buildRandom().toString()
        );
    }
}
