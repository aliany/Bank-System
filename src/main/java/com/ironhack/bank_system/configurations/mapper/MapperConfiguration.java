package com.ironhack.bank_system.configurations.mapper;

import com.ironhack.bank_system.dto.*;
import com.ironhack.bank_system.enums.Rol;
import com.ironhack.bank_system.model.*;
import org.javamoney.moneta.Money;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.money.Monetary;

@Configuration
public class MapperConfiguration {

    @Bean
    public CustomMapper modelMapper() {
        CustomMapper modelMapper = new CustomMapper();
        modelMapper
                .getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper
                .getConfiguration()
                .setPropertyCondition(Conditions.isNotNull());
        Condition notNull = ctx -> ctx.getSource() != null;
        Condition isNull = ctx -> ctx.getSource() == null;
        //ACCOUNT
        modelMapper
                .typeMap(AccountDTO.class, Account.class)
                .addMappings(mapper -> mapper.using(DoubleToMoney).map(AccountDTO::getBalanceQty, Account::setBalance));
        modelMapper
                .typeMap(Account.class, AccountDTO.class)
                .addMappings(mapper -> mapper.using(MoneyToString).map(Account::getBalance, AccountDTO::setBalanceFormatted))
                .addMappings(mapper -> mapper.using(MoneyToString).map(Account::getPenaltyFee, AccountDTO::setPenaltyFeeFormatted))
                .addMappings(mapper -> mapper.using(MoneyToDouble).map(Account::getBalance, AccountDTO::setBalanceQty));

        //CHECKING
        modelMapper
                .typeMap(CheckingDTO.class, Checking.class)
                .addMappings(mapper -> mapper.using(DoubleToMoney).map(CheckingDTO::getBalanceQty, Checking::setBalance))
                .addMappings(mapper -> mapper.using(DoubleToMoney).map(CheckingDTO::getMinimumBalanceQty, Checking::setMinimumBalance));
        modelMapper
                .typeMap(Checking.class, CheckingDTO.class)
                .addMappings(mapper -> mapper.using(MoneyToString).map(Checking::getBalance, CheckingDTO::setBalanceFormatted))
                .addMappings(mapper -> mapper.using(MoneyToString).map(Checking::getPenaltyFee, CheckingDTO::setPenaltyFeeFormatted))
                .addMappings(mapper -> mapper.using(MoneyToString).map(Checking::getMinimumBalance, CheckingDTO::setMinimumBalanceFormatted))
                .addMappings(mapper -> mapper.using(MoneyToString).map(Checking::getMonthlyMaintenanceFee, CheckingDTO::setMonthlyMaintenanceFeeFormatted))
                .addMappings(mapper -> mapper.using(MoneyToDouble).map(Checking::getBalance, CheckingDTO::setBalanceQty))
                .addMappings(mapper -> mapper.using(MoneyToDouble).map(Checking::getMinimumBalance, CheckingDTO::setMinimumBalanceQty));

        //STUDENTCHECKING
        modelMapper
                .typeMap(StudentCheckingDTO.class, StudentChecking.class)
                .addMappings(mapper -> mapper.using(DoubleToMoney).map(StudentCheckingDTO::getBalanceQty, StudentChecking::setBalance));
        modelMapper
                .typeMap(StudentChecking.class, StudentCheckingDTO.class)
                .addMappings(mapper -> mapper.using(MoneyToString).map(StudentChecking::getBalance, StudentCheckingDTO::setBalanceFormatted))
                .addMappings(mapper -> mapper.using(MoneyToString).map(StudentChecking::getPenaltyFee, StudentCheckingDTO::setPenaltyFeeFormatted))
                .addMappings(mapper -> mapper.using(MoneyToDouble).map(StudentChecking::getBalance, StudentCheckingDTO::setBalanceQty));

        //SAVING
        modelMapper
                .typeMap(SavingDTO.class, Saving.class)
                .addMappings(mapper -> mapper.using(DoubleToMoney).map(SavingDTO::getBalanceQty, Saving::setBalance))
                .addMappings(mapper -> mapper.using(DoubleToMoney).map(SavingDTO::getMinimumBalanceQty, Saving::setMinimumBalance));
        modelMapper
                .typeMap(Saving.class, SavingDTO.class)
                .addMappings(mapper -> mapper.using(MoneyToString).map(Saving::getBalance, SavingDTO::setBalanceFormatted))
                .addMappings(mapper -> mapper.using(MoneyToString).map(Saving::getPenaltyFee, SavingDTO::setPenaltyFeeFormatted))
                .addMappings(mapper -> mapper.using(MoneyToDouble).map(Saving::getBalance, SavingDTO::setBalanceQty))
                .addMappings(mapper -> mapper.using(MoneyToDouble).map(Saving::getMinimumBalance, SavingDTO::setMinimumBalanceQty));

        //CREDITCARD
        modelMapper
                .typeMap(CreditCardDTO.class, CreditCard.class)
                .addMappings(mapper -> mapper.using(DoubleToMoney).map(CreditCardDTO::getBalanceQty, CreditCard::setBalance))
                .addMappings(mapper -> mapper.using(DoubleToMoney).map(CreditCardDTO::getCreditLimitQty, CreditCard::setCreditLimit));
        modelMapper
                .typeMap(CreditCard.class, CreditCardDTO.class)
                .addMappings(mapper -> mapper.using(MoneyToString).map(CreditCard::getBalance, CreditCardDTO::setBalanceFormatted))
                .addMappings(mapper -> mapper.using(MoneyToString).map(CreditCard::getPenaltyFee, CreditCardDTO::setPenaltyFeeFormatted))
                .addMappings(mapper -> mapper.using(MoneyToString).map(CreditCard::getCreditLimit, CreditCardDTO::setCreditLimitFormatted))
                .addMappings(mapper -> mapper.using(MoneyToDouble).map(CreditCard::getBalance, CreditCardDTO::setBalanceQty))
                .addMappings(mapper -> mapper.using(MoneyToDouble).map(CreditCard::getCreditLimit, CreditCardDTO::setCreditLimitQty));

        //TRANSFER
        modelMapper
                .typeMap(TransferDTO.class, Transfer.class)
                .addMappings(mapper -> mapper.using(DoubleToMoney).map(TransferDTO::getAmountQty, Transfer::setAmount));
        modelMapper
                .typeMap(Transfer.class, TransferDTO.class)
                .addMappings(mapper -> mapper.using(MoneyToString).map(Transfer::getAmount, TransferDTO::setAmountFormatted))
                .addMappings(mapper -> mapper.using(MoneyToDouble).map(Transfer::getAmount, TransferDTO::setAmountQty))
                .addMappings(mapper -> mapper.when(notNull).map(Transfer::getAccountFrom, TransferDTO::setAccountFromName))
                .addMappings(mapper -> mapper.when(isNull).skip(TransferDTO::setAccountFrom))
                .addMappings(mapper -> mapper.when(isNull).skip(TransferDTO::setAccountFromName));

        return modelMapper;
    }


    Converter<Double, Money> DoubleToMoney = new AbstractConverter<Double, Money>() {
        protected Money convert(Double source) {
            return Money.of(source, Monetary.getCurrency("EUR"));
        }
    };

    Converter<Money, Double> MoneyToDouble = new AbstractConverter<Money, Double>() {
        protected Double convert(Money source) {
            return source.getNumber().doubleValue();
        }
    };

    Converter<Money, String> MoneyToString = new AbstractConverter<Money, String>() {
        protected String convert(Money source) {
            return source.toString();
        }
    };

}
