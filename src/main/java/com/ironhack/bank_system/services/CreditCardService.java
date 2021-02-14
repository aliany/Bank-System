package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.AccountHoldersDTO;
import com.ironhack.bank_system.dto.CheckingDTO;
import com.ironhack.bank_system.dto.CreditCardDTO;
import com.ironhack.bank_system.repository.AccountHoldersRepository;
import com.ironhack.bank_system.repository.CheckingRepository;
import com.ironhack.bank_system.repository.CreditCardRepository;
import org.javamoney.moneta.Money;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.money.Monetary;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CreditCardService extends BaseService {

    private final CreditCardRepository creditCardRepository;
    private final AccountHoldersRepository accountHoldersRepository;
    private final CustomMapper modelMapper;

    @Autowired
    public CreditCardService(
            CreditCardRepository creditCardRepository,
            AccountHoldersRepository accountHoldersRepository,
            CustomMapper modelMapper
    ) {
        super(modelMapper);
        this.creditCardRepository = creditCardRepository;
        this.accountHoldersRepository = accountHoldersRepository;
        this.modelMapper = modelMapper;

    }

    public List<CreditCardDTO> findAll() {
        return creditCardRepository
                .findAll()
                .stream()
                .map(creditCardBD -> toDto(creditCardBD))
                .collect(Collectors.toList());
    }

    public CreditCardDTO findAccountById(Long id) {
        CreditCardDTO creditCardDTO = creditCardRepository.findById(id)
                .stream()
                .map(creditCardBD -> this.toDto(creditCardBD))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(creditCardDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not found.");
        }
        return creditCardDTO;
    }

    public CreditCardDTO create(CreditCardDTO creditCardDTO) {
        try {
            if (Objects.isNull(creditCardDTO.getPrimaryOwnerId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account need at least one owner.");
            } else {
                //   Find the 1st Owner
                AccountHoldersDTO primaryOwnerDTO = accountHoldersRepository
                        .findById(creditCardDTO.getPrimaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(primaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The primary owner was not founded.");
                }
                if (!Objects.isNull(creditCardDTO.getSecondaryOwnerId())) {
                    //   Find the 2nd Owner
                    AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                            .findById(creditCardDTO.getSecondaryOwnerId())
                            .stream()
                            .map(accountHolderBD -> toDto(accountHolderBD))
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(secondaryOwnerDTO)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                    } else {
                        creditCardDTO.setSecondaryOwner(secondaryOwnerDTO);
                    }
                }
                creditCardDTO.setPrimaryOwner(primaryOwnerDTO);
                CreditCardDTO resultDto = Optional.of(creditCardDTO)
                        .map(tDto -> toEntity(tDto))
                        .map(creditCardRepository::save)
                        .map(entity -> toDto(entity))
                        .orElse(null);
                if (Objects.isNull(resultDto)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not created.");
                }
                return resultDto;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not created.");
        }
    }

    public CreditCardDTO update(long id, CreditCardDTO creditCardDTO) {
        try {
            if (!Objects.isNull(creditCardDTO.getSecondaryOwnerId())) {
                //   Find the 2nd Owner if we need change
                AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                        .findById(creditCardDTO.getSecondaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(secondaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                } else {
                    creditCardDTO.setSecondaryOwner(secondaryOwnerDTO);
                }
            }
            CreditCardDTO resultDto = creditCardRepository.findById(id)
                    .map(entity -> {
                        entity.setBalance(Money.of(creditCardDTO.getBalanceQty(), Monetary.getCurrency("EUR")));
                        entity.setCreditLimit(Money.of(creditCardDTO.getCreditLimitQty(), Monetary.getCurrency("EUR")));
                        entity.setInterestRate(creditCardDTO.getInterestRate());
                        if (!Objects.isNull(creditCardDTO.getSecondaryOwner())) {
                            entity.setSecondaryOwner(
                                    Optional.of(creditCardDTO.getSecondaryOwner())
                                            .map(ahDto -> toEntity(ahDto)).get()
                            );
                        }
                        return creditCardRepository.save(entity);
                    })
                    .map(entity -> toDto(entity))
                    .orElse(null);
            if (Objects.isNull(resultDto)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not updated.");
            }
            return resultDto;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not updated.");
        }
    }

    public void delete(Long id) {
        try {
            creditCardRepository.deleteById(id);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not deleted.");
        }
    }

}
