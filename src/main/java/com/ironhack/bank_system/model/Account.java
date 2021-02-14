package com.ironhack.bank_system.model;

import com.ironhack.bank_system.convert.MoneyConverter;
import com.ironhack.bank_system.enums.AccountType;
import com.ironhack.bank_system.enums.Status;
import com.ironhack.bank_system.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.javamoney.moneta.Money;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.money.Monetary;
import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String iban;
    @NotNull
    @Convert(converter = MoneyConverter.class)
    @Digits(integer = 19, fraction = 4)
    private Money balance;
    @NotNull
    @ManyToOne
    private AccountHolders primaryOwner;
    @ManyToOne
    private AccountHolders secondaryOwner;
    @Builder.Default
    @NotNull
    @Convert(converter = MoneyConverter.class)
    @Digits(integer = 19, fraction = 4)
    private Money penaltyFee = Money.of(40, Monetary.getCurrency("EUR"));
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    private Date penaltyFeeApplied;
    @Column(unique = true)
    private String secretKey;
    @CreationTimestamp
    private Date createdDate;
    @UpdateTimestamp
    private Date updatedDate;

    @Transient
    public AccountType getAccountType() {
        return AccountType.valueOf(this.getClass().getAnnotation(DiscriminatorValue.class).value());
    }
}
