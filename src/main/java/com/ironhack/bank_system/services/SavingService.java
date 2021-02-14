package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.AccountHoldersDTO;
import com.ironhack.bank_system.dto.SavingDTO;
import com.ironhack.bank_system.dto.StudentCheckingDTO;
import com.ironhack.bank_system.model.AccountHolders;
import com.ironhack.bank_system.model.Saving;
import com.ironhack.bank_system.model.StudentChecking;
import com.ironhack.bank_system.repository.AccountHoldersRepository;
import com.ironhack.bank_system.repository.SavingRepository;
import com.ironhack.bank_system.repository.StudentCheckingRepository;
import org.javamoney.moneta.Money;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.money.Monetary;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavingService extends BaseService {

    private final SavingRepository savingRepository;
    private final AccountHoldersRepository accountHoldersRepository;
    private final CustomMapper modelMapper;

    @Autowired
    public SavingService(
            SavingRepository savingRepository,
            AccountHoldersRepository accountHoldersRepository,
            CustomMapper modelMapper
    ) {
        super(modelMapper);
        this.savingRepository = savingRepository;
        this.accountHoldersRepository = accountHoldersRepository;
        this.modelMapper = modelMapper;
    }

    public List<SavingDTO> findAll() {
        return savingRepository
                .findAll()
                .stream()
                .map(savingDB -> this.toDto(savingDB))
                .collect(Collectors.toList());
    }

    public SavingDTO findAccountById(Long id) {
        SavingDTO savingDTO = savingRepository.findById(id)
                .stream()
                .map(savingDB -> this.toDto(savingDB))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(savingDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not found.");
        }
        return savingDTO;
    }

    public SavingDTO create(SavingDTO savingDTO) {
        try {
            if (Objects.isNull(savingDTO.getPrimaryOwnerId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account need at least one owner.");
            } else {
                //   Find the 1st Owner
                AccountHoldersDTO primaryOwnerDTO = accountHoldersRepository
                        .findById(savingDTO.getPrimaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(primaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The primary owner was not founded.");
                }
                if (!Objects.isNull(savingDTO.getSecondaryOwnerId())) {
                    //   Find the 2nd Owner
                    AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                            .findById(savingDTO.getSecondaryOwnerId())
                            .stream()
                            .map(accountHolderBD -> toDto(accountHolderBD))
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(secondaryOwnerDTO)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                    } else {
                        savingDTO.setSecondaryOwner(secondaryOwnerDTO);
                    }
                }
                savingDTO.setPrimaryOwner(primaryOwnerDTO);
                SavingDTO resultDto = Optional.of(savingDTO)
                        .map(tDto -> toEntity(tDto))
                        .map(savingRepository::save)
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

    public SavingDTO update(long id, SavingDTO savingDTO) {
        try {
            if (!Objects.isNull(savingDTO.getSecondaryOwnerId())) {
                //   Find the 2nd Owner if we need change
                AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                        .findById(savingDTO.getSecondaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(secondaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                } else {
                    savingDTO.setSecondaryOwner(secondaryOwnerDTO);
                }
            }
            SavingDTO resultDto = savingRepository.findById(id)
                    .map(entity -> {
                        entity.setBalance(Money.of(savingDTO.getBalanceQty(), Monetary.getCurrency("EUR")));
                        entity.setMinimumBalance(Money.of(savingDTO.getMinimumBalanceQty(), Monetary.getCurrency("EUR")));
                        entity.setInterestRate(savingDTO.getInterestRate());
                        if (!Objects.isNull(savingDTO.getSecondaryOwner())) {
                            entity.setSecondaryOwner(
                                    Optional.of(savingDTO.getSecondaryOwner())
                                            .map(ahDto -> toEntity(ahDto)).get()
                            );
                        }
                        return savingRepository.save(entity);
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
            savingRepository.deleteById(id);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not deleted.");
        }
    }
}
