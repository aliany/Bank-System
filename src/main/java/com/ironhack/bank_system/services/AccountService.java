package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.*;
import com.ironhack.bank_system.enums.AccountType;
import com.ironhack.bank_system.enums.TransferType;
import com.ironhack.bank_system.model.*;
import com.ironhack.bank_system.repository.AccountHoldersRepository;
import com.ironhack.bank_system.repository.AccountRepository;
import com.ironhack.bank_system.repository.CreditCardRepository;
import com.ironhack.bank_system.repository.TransferRepository;
import com.ironhack.bank_system.utils.Utils;
import org.javamoney.moneta.Money;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import javax.money.Monetary;
import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AccountService extends BaseService {

    private final TransferService transferService;
    private final AccountRepository accountRepository;
    private final AccountHoldersRepository accountHoldersRepository;
    private final CreditCardRepository creditCardRepository;
    private final CustomMapper modelMapper;

    @Autowired
    public AccountService(
            AccountRepository accountRepository,
            AccountHoldersRepository accountHoldersRepository,
            TransferService transferService,
            CreditCardRepository creditCardRepository,
            CustomMapper modelMapper
    ) {
        super(modelMapper);
        this.accountRepository = accountRepository;
        this.accountHoldersRepository = accountHoldersRepository;
        this.transferService = transferService;
        this.creditCardRepository = creditCardRepository;
        this.modelMapper = modelMapper;
    }

    public List<AccountDTO> findAll() {
        return this.accountRepository
                .findAll()
                .stream()
                .map(accountBD -> toDto(accountBD))
                .collect(Collectors.toList());
    }

    //Search a ditch by id
    public AccountDTO findAccountById(Long id) {
        AccountDTO accountDTO = accountRepository.findById(id)
                .stream()
                .map(accountBD -> this.toDto(accountBD))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(accountDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not found.");
        }
        return accountDTO;
    }

    public AccountDTO create(AccountDTO accountDTO) {
        try {
            if (Objects.isNull(accountDTO.getPrimaryOwnerId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account need at least one owner.");
            } else {
                //   Find the 1st Owner
                AccountHoldersDTO primaryOwnerDTO = accountHoldersRepository
                        .findById(accountDTO.getPrimaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(primaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The primary owner was not founded.");
                }
                if (!Objects.isNull(accountDTO.getSecondaryOwnerId())) {
                    //   Find the 2nd Owner
                    AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                            .findById(accountDTO.getSecondaryOwnerId())
                            .stream()
                            .map(accountHolderBD -> toDto(accountHolderBD))
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(secondaryOwnerDTO)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                    } else {
                        accountDTO.setSecondaryOwner(secondaryOwnerDTO);
                    }
                }
                accountDTO.setPrimaryOwner(primaryOwnerDTO);
                AccountDTO resultDto = Optional.of(accountDTO)
                        .map(tDto -> toEntity(tDto))
                        .map(accountRepository::save)
                        .map(entity -> toDto(entity))
                        .orElse(null);
                if (Objects.isNull(resultDto)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not created.");
                }
                return resultDto;
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not created.");
        }
    }

    public AccountDTO update(long id, AccountDTO accountDTO) {
        try {
            if (!Objects.isNull(accountDTO.getSecondaryOwnerId())) {
                //   Find the 2nd Owner if we need change
                AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                        .findById(accountDTO.getSecondaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(secondaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                } else {
                    accountDTO.setSecondaryOwner(secondaryOwnerDTO);
                }
            }
            AccountDTO resultDto = accountRepository.findById(id)
                    .map(entity -> {
                        entity.setBalance(Money.of(accountDTO.getBalanceQty(), Monetary.getCurrency("EUR")));
                        if (!Objects.isNull(accountDTO.getSecondaryOwner())) {
                            entity.setSecondaryOwner(
                                    Optional.of(accountDTO.getSecondaryOwner())
                                            .map(ahDto -> toEntity(ahDto)).get()
                            );
                        }
                        return accountRepository.save(entity);
                    })
                    .map(entity -> toDto(entity))
                    .orElse(null);
            if (Objects.isNull(resultDto)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not updated.");
            }
            return resultDto;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not updated.");
        }
    }

    public void delete(Long id) {
        try {
            accountRepository.deleteById(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not deleted.");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public Object getBalance(String iban) {
        try {
            List<Date> rateDates = new ArrayList<>();
            Account account = accountRepository.findByIban(iban).orElse(null);
            if (Objects.isNull(account)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The balance cannot be get.");
            }
            switch (account.getAccountType()) {
                case SAVING:
                    if (Utils.getDiffYears(account.getCreatedDate(), new Date()) < 1) {
                        return Optional.of(account).map(accountDB -> toDto(accountDB)).get();
                    }
                    Transfer transfer = transferService
                            .findByAccountToAndType(account.getId(), TransferType.RATE)
                            .stream()
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(transfer)) {
                        Date dateCreated = Utils.getDateAfterSum(account.getCreatedDate(), 1, Calendar.YEAR);
                        while (dateCreated.before(new Date()) || dateCreated.equals(new Date())) {
                            dateCreated = Utils.getDateAfterSum(dateCreated, 1, Calendar.YEAR);
                            rateDates.add(dateCreated);
                        }
                        rateDates.forEach(date -> {
                            Money balance = accountRepository.getBalance(iban).orElse(null);
                            if (!Objects.isNull(balance)) {
                                Double amount = ((Saving) account)
                                        .getInterestRate()
                                        .multiply(new BigDecimal(balance.getNumber().doubleValue()))
                                        .setScale(4, RoundingMode.HALF_UP)
                                        .doubleValue();
                                transferService.create(
                                        TransferDTO
                                                .builder()
                                                .accountTo(
                                                        Optional.of(account)
                                                                .map(accountDB -> toDto(accountDB))
                                                                .get()
                                                )
                                                .accountFromName(account.getPrimaryOwner().getName())
                                                .amountQty(amount)
                                                .date(new Date())
                                                .concept("Interest income.")
                                                .type(TransferType.RATE)
                                                .build()
                                );
                                account.setBalance(balance.add(Money.of(amount, Monetary.getCurrency("EUR"))));
                                accountRepository.save(account);
                            } else {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The balance cannot be get.");
                            }
                        });
                        return Optional.of((Saving) account)
                                .map(accountDB -> toDto(accountDB))
                                .get();
                    }
                    Date dateCreated = Utils.getDateAfterSum(transfer.getCreatedDate(), 1, Calendar.YEAR);
                    while (dateCreated.before(new Date()) || dateCreated.equals(new Date())) {
                        dateCreated = Utils.getDateAfterSum(dateCreated, 1, Calendar.YEAR);
                        rateDates.add(dateCreated);
                    }
                    rateDates.forEach(date -> {
                        Money balance = accountRepository.getBalance(iban).orElse(null);
                        if (!Objects.isNull(balance)) {
                            Double amount = ((Saving) account)
                                    .getInterestRate()
                                    .multiply(new BigDecimal(balance.getNumber().doubleValue()))
                                    .setScale(4, RoundingMode.HALF_UP)
                                    .doubleValue();
                            transferService.create(
                                    TransferDTO
                                            .builder()
                                            .accountTo(
                                                    Optional.of(account)
                                                            .map(accountDB -> toDto(accountDB))
                                                            .get()
                                            )
                                            .accountFromName(account.getPrimaryOwner().getName())
                                            .amountQty(amount)
                                            .date(new Date())
                                            .concept("Interest income.")
                                            .type(TransferType.RATE)
                                            .build()
                            );
                            account.setBalance(balance.add(Money.of(amount, Monetary.getCurrency("EUR"))));
                            accountRepository.save(account);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The balance cannot be get.");
                        }
                    });
                    return Optional.of((Saving) account)
                            .map(accountDB -> toDto(accountDB))
                            .get();
                case CREDITCARD:
                    if (Utils.getDiffMonths(account.getCreatedDate(), new Date()) < 1) {
                        return Optional.of(account).map(accountDB -> toDto(accountDB)).get();
                    }
                    transfer = transferService
                            .findByAccountToAndType(account.getId(), TransferType.RATE)
                            .stream()
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(transfer)) {
                        dateCreated = Utils.getDateAfterSum(account.getCreatedDate(), 1, Calendar.MONTH);
                        while (dateCreated.before(new Date()) || dateCreated.equals(new Date())) {
                            dateCreated = Utils.getDateAfterSum(dateCreated, 1, Calendar.MONTH);
                            rateDates.add(dateCreated);
                        }
                        rateDates.forEach(date -> {
                            Money balance = accountRepository.getBalance(iban).orElse(null);
                            if (!Objects.isNull(balance)) {
                                Double amount = ((CreditCard) account)
                                        .getInterestRate()
                                        .multiply(new BigDecimal(balance.getNumber().doubleValue()))
                                        .setScale(4, RoundingMode.HALF_UP)
                                        .doubleValue();
                                transferService.create(TransferDTO
                                        .builder()
                                        .accountTo(
                                                Optional.of(account)
                                                        .map(accountDB -> toDto(accountDB))
                                                        .get()
                                        )
                                        .accountToName(account.getPrimaryOwner().getName())
                                        .amountQty(amount)
                                        .date(new Date())
                                        .concept("Interest income.")
                                        .type(TransferType.RATE)
                                        .build());
                                account.setBalance(balance.add(Money.of(amount, Monetary.getCurrency("EUR"))));
                                accountRepository.save(account);
                            } else {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The balance cannot be get.");
                            }
                        });
                        return Optional.of((CreditCard) account)
                                .map(accountDB -> toDto(accountDB))
                                .get();
                    }
                    dateCreated = Utils.getDateAfterSum(transfer.getCreatedDate(), 1, Calendar.MONTH);
                    while (dateCreated.before(new Date()) || dateCreated.equals(new Date())) {
                        dateCreated = Utils.getDateAfterSum(dateCreated, 1, Calendar.MONTH);
                        rateDates.add(dateCreated);
                    }
                    rateDates.forEach(date -> {
                        Money balance = accountRepository.getBalance(iban).orElse(null);
                        if (!Objects.isNull(balance)) {
                            Double amount = ((CreditCard) account)
                                    .getInterestRate()
                                    .multiply(new BigDecimal(balance.getNumber().doubleValue()))
                                    .setScale(4, RoundingMode.HALF_UP)
                                    .doubleValue();
                            transferService.create(TransferDTO
                                    .builder()
                                    .accountTo(
                                            Optional.of(account)
                                                    .map(accountDB -> toDto(accountDB))
                                                    .get()
                                    )
                                    .accountToName(account.getPrimaryOwner().getName())
                                    .amountQty(amount)
                                    .date(new Date())
                                    .concept("Interest income.")
                                    .type(TransferType.RATE)
                                    .build());
                            account.setBalance(
                                    account
                                            .getBalance()
                                            .add(Money.of(amount, Monetary.getCurrency("EUR")))
                            );
                            accountRepository.save(account);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The balance cannot be get.");
                        }
                    });
                    return Optional.of((CreditCard) account)
                            .map(accountDB -> toDto(accountDB))
                            .get();
                case CHECKING:
                    return Optional.of((Checking) account)
                            .map(accountDB -> toDto(accountDB))
                            .get();
                case STUDENTCHECKING:
                    return Optional.of((StudentChecking) account)
                            .map(accountDB -> toDto(accountDB))
                            .get();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The balance cannot be get.");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The balance cannot be get.");
    }

}
