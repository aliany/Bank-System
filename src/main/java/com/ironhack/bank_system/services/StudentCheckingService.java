package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.AccountDTO;
import com.ironhack.bank_system.dto.AccountHoldersDTO;
import com.ironhack.bank_system.dto.CheckingDTO;
import com.ironhack.bank_system.dto.StudentCheckingDTO;
import com.ironhack.bank_system.model.AccountHolders;
import com.ironhack.bank_system.model.StudentChecking;
import com.ironhack.bank_system.repository.AccountHoldersRepository;
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
public class StudentCheckingService extends BaseService {

    private final StudentCheckingRepository studentCheckingRepository;
    private final AccountHoldersRepository accountHoldersRepository;
    private final CustomMapper modelMapper;

    @Autowired
    public StudentCheckingService(
            StudentCheckingRepository studentCheckingRepository,
            AccountHoldersRepository accountHoldersRepository,
            CustomMapper modelMapper
    ) {
        super(modelMapper);
        this.studentCheckingRepository = studentCheckingRepository;
        this.accountHoldersRepository = accountHoldersRepository;
        this.modelMapper = modelMapper;
    }

    public List<StudentCheckingDTO> findAll() {
        return studentCheckingRepository
                .findAll()
                .stream()
                .map(studentCheckingBD -> this.toDto(studentCheckingBD))
                .collect(Collectors.toList());
    }

    public StudentCheckingDTO findAccountById(Long id) {
        StudentCheckingDTO studentCheckingDTO = studentCheckingRepository.findById(id)
                .stream()
                .map(studentCheckingBD -> this.toDto(studentCheckingBD))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(studentCheckingDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not found.");
        }
        return studentCheckingDTO;
    }

    public CheckingDTO createAsStudent(AccountDTO accountDTO) {
        StudentCheckingDTO studentCheckingDTOCreated = create(
                Optional.of(accountDTO)
                        .map(accDTO -> AccountDTOtoStudentCheckingDTO(accDTO))
                        .get()
        );
        return Optional.of(studentCheckingDTOCreated)
                .map(studentDto -> {
                    CheckingDTO checkingDTO = StudentCheckingDTOtoCheckingDTO(studentDto);
                    checkingDTO.setMinimumBalanceFormatted("");
                    checkingDTO.setMinimumBalanceQty(null);
                    checkingDTO.setMonthlyMaintenanceFeeFormatted("");
                    checkingDTO.setMonthlyMaintenanceFeeQty(null);
                    return checkingDTO;
                }).get();
    }

    public StudentCheckingDTO create(StudentCheckingDTO studentCheckingDTO) {
        try {
            if (Objects.isNull(studentCheckingDTO.getPrimaryOwnerId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account need at least one owner.");
            } else {
                //   Find the 1st Owner
                AccountHoldersDTO primaryOwnerDTO = !Objects.isNull(studentCheckingDTO.getPrimaryOwner())
                        ? studentCheckingDTO.getPrimaryOwner()
                        : accountHoldersRepository
                        .findById(studentCheckingDTO.getPrimaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(primaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The primary owner was not founded.");
                }
                if (!Objects.isNull(studentCheckingDTO.getSecondaryOwnerId())) {
                    //   Find the 2nd Owner
                    AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                            .findById(studentCheckingDTO.getSecondaryOwnerId())
                            .stream()
                            .map(accountHolderBD -> toDto(accountHolderBD))
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(secondaryOwnerDTO)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                    } else {
                        studentCheckingDTO.setSecondaryOwner(secondaryOwnerDTO);
                    }
                }
                studentCheckingDTO.setPrimaryOwner(primaryOwnerDTO);
                StudentCheckingDTO resultDto = Optional.of(studentCheckingDTO)
                        .map(tDto -> toEntity(tDto))
                        .map(studentCheckingRepository::save)
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

    public StudentCheckingDTO update(long id, StudentCheckingDTO studentCheckingDTO) {
        try {
            if (!Objects.isNull(studentCheckingDTO.getSecondaryOwnerId())) {
                //   Find the 2nd Owner if we need change
                AccountHoldersDTO secondaryOwnerDTO = accountHoldersRepository
                        .findById(studentCheckingDTO.getSecondaryOwnerId())
                        .stream()
                        .map(accountHolderBD -> toDto(accountHolderBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(secondaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The secundary owner was not founded.");
                } else {
                    studentCheckingDTO.setSecondaryOwner(secondaryOwnerDTO);
                }
            }
            StudentCheckingDTO resultDto = studentCheckingRepository.findById(id)
                    .map(entity -> {
                        entity.setBalance(Money.of(studentCheckingDTO.getBalanceQty(), Monetary.getCurrency("EUR")));
                        if (!Objects.isNull(studentCheckingDTO.getSecondaryOwner())) {
                            entity.setSecondaryOwner(
                                    Optional.of(studentCheckingDTO.getSecondaryOwner())
                                            .map(ahDto -> toEntity(ahDto)).get()
                            );
                        }
                        return studentCheckingRepository.save(entity);
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
            studentCheckingRepository.deleteById(id);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not deleted.");
        }
    }
}
