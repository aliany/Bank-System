package com.ironhack.bank_system.model;

import com.ironhack.bank_system.convert.MoneyConverter;
import com.ironhack.bank_system.enums.Rol;
import com.ironhack.bank_system.enums.TransferType;
import com.ironhack.bank_system.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EnableAutoConfiguration
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    private Account accountFrom;
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    private Account accountTo;
    private String accountToName;
    private String concept;
    @Builder.Default
    @NotNull
    @Convert(converter = MoneyConverter.class)
    @Digits(integer = 19, fraction = 4)
    private Money amount = Money.of(0, Monetary.getCurrency("EUR"));
    @Builder.Default
    @NotNull
    private Date date = new Date();
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private TransferType type = TransferType.RATE;
    @CreationTimestamp
    private Date createdDate;

    @PrePersist
    private void beforeCreate() {
        setAccountToName(accountTo.getPrimaryOwner().getName());
    }


}
