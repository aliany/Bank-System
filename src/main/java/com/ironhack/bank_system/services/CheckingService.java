package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.AccountDTO;
import com.ironhack.bank_system.dto.AccountHoldersDTO;
import com.ironhack.bank_system.dto.CheckingDTO;
import com.ironhack.bank_system.enums.Status;
import com.ironhack.bank_system.model.Account;
import com.ironhack.bank_system.model.AccountHolders;
import com.ironhack.bank_system.model.Checking;
import com.ironhack.bank_system.repository.AccountHoldersRepository;
import com.ironhack.bank_system.repository.CheckingRepository;
import com.ironhack.bank_system.utils.Utils;
import org.javamoney.moneta.Money;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.money.Monetary;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CheckingService extends BaseService {

    private final CheckingRepository checkingRepository;
    private final AccountHoldersRepository accountHoldersRepository;
    private final CustomMapper modelMapper;

    @Autowired
    public CheckingService(
            CheckingRepository checkingRepository,
            AccountHoldersRepository accountHoldersRepository,
            CustomMapper modelMapper
    ) {
        super(modelMapper);
        this.checkingRepository = checkingRepository;
        this.accountHoldersRepository = accountHoldersRepository;
        this.modelMapper = modelMapper;

    }

    public List<CheckingDTO> findAll() {
        return checkingRepository
                .findAll()
                .stream()
                .map(checkingBD -> toDto(checkingBD))
                .collect(Collectors.toList());
    }

    public CheckingDTO findAccountById(Long id) {
        CheckingDTO checkingDTO = checkingRepository.findById(id)
                .stream()
                .map(checkingBD -> this.toDto(checkingBD))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(checkingDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not found.");
        }
        return checkingDTO;
    }

    public CheckingDTO create(AccountDTO accountDTO) {
        try {
            CheckingDTO checkingDTO = Optional.of(accountDTO)
                    .map(accDTO -> AccountDTOtoCheckingDTO(accDTO))
                    .get();
            if (!Objects.isNull(checkingDTO.getSecondaryOwnerId())) {
                //   Find the 2nd Owner
                AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                        .findById(checkingDTO.getSecondaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(secondaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                } else {
                    checkingDTO.setSecondaryOwner(secondaryOwnerDTO);
                }
            }
            CheckingDTO resultDto = Optional.of(checkingDTO)
                    .map(tDto -> toEntity(tDto))
                    .map(checkingRepository::save)
                    .map(entity -> toDto(entity))
                    .orElse(null);
            if (Objects.isNull(resultDto)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not created.");
            }
            return resultDto;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not created.");
        }
    }

    public CheckingDTO update(long id, CheckingDTO checkingDTO) {
        try {
            if (!Objects.isNull(checkingDTO.getSecondaryOwnerId())) {
                //   Find the 2nd Owner if we need change
                AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                        .findById(checkingDTO.getSecondaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(secondaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                } else {
                    checkingDTO.setSecondaryOwner(secondaryOwnerDTO);
                }
            }
            CheckingDTO resultDto = checkingRepository.findById(id)
                    .map(entity -> {
                        entity.setBalance(Money.of(checkingDTO.getBalanceQty(), Monetary.getCurrency("EUR")));
                        entity.setMinimumBalance(Money.of(checkingDTO.getMinimumBalanceQty(), Monetary.getCurrency("EUR")));
                        if (!Objects.isNull(checkingDTO.getSecondaryOwner())) {
                            entity.setSecondaryOwner(
                                    Optional.of(checkingDTO.getSecondaryOwner())
                                            .map(ahDto -> toEntity(ahDto)).get()
                            );
                        }
                        return checkingRepository.save(entity);
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
            checkingRepository.deleteById(id);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not deleted.");
        }
    }

}
