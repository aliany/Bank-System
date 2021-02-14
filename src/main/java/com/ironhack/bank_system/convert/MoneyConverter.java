package com.ironhack.bank_system.convert;

import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;

@Converter
public class MoneyConverter implements AttributeConverter<Money, BigDecimal> {

    private static final CurrencyUnit eur = Monetary.getCurrency("EUR");

    @Override
    public BigDecimal convertToDatabaseColumn(Money monetaryAmount) {
        return monetaryAmount.getNumberStripped();
    }

    @Override
    public Money convertToEntityAttribute(BigDecimal dbData) {
        return Money.of(dbData, eur);
    }
}
