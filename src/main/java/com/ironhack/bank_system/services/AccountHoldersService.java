package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.AccountDTO;
import com.ironhack.bank_system.dto.AccountHoldersDTO;
import com.ironhack.bank_system.dto.AddressDTO;
import com.ironhack.bank_system.dto.UserDTO;
import com.ironhack.bank_system.model.AccountHolders;
import com.ironhack.bank_system.model.Address;
import com.ironhack.bank_system.model.User;
import com.ironhack.bank_system.repository.AccountHoldersRepository;
import com.ironhack.bank_system.repository.AddressRepository;
import com.ironhack.bank_system.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountHoldersService extends BaseService {


    private final AccountHoldersRepository accountHoldersRepository;
    private final CustomMapper modelMapper;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountHoldersService(
            AccountHoldersRepository accountHoldersRepository,
            AddressRepository addressRepository,
            CustomMapper modelMapper,
            UserRepository userRepository
    ) {
        super(modelMapper);
        this.accountHoldersRepository = accountHoldersRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public List<AccountHoldersDTO> findAll() {
        return accountHoldersRepository.findAll()
                .stream()
                .map(accountHoldersBD -> toDto(accountHoldersBD))
                .collect(Collectors.toList());
    }

    //Search a ditch by id
    public AccountHoldersDTO findAccountHoldersById(Long id) {
        AccountHoldersDTO accountHoldersDTO = accountHoldersRepository.findById(id)
                .stream()
                .map(accountHoldersBD -> this.toDto(accountHoldersBD))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(accountHoldersDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not found.");
        }
        return accountHoldersDTO;

    }

    public AccountHoldersDTO create(AccountHoldersDTO accountHoldersDTO) {
        try {
            if (Objects.isNull(accountHoldersDTO.getPrimaryAddressId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account holders need at least one address.");
            } else {
                //   Find the 1st Address
                AddressDTO primaryAddress = addressRepository
                        .findById(accountHoldersDTO.getPrimaryAddressId())
                        .stream()
                        .map(addressBD -> toDto(addressBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(primaryAddress)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The primary address was not founded.");
                }
                if (!Objects.isNull(accountHoldersDTO.getMailingAddressId())) {
                    //   Find the 2nd Address
                    AddressDTO mailingAddress = addressRepository
                            .findById(accountHoldersDTO.getMailingAddressId())
                            .stream()
                            .map(addressBD -> toDto(addressBD))
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(mailingAddress)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The mailing address was not founded.");
                    } else {
                        accountHoldersDTO.setMailingAddress(mailingAddress);
                    }
                }
                if (Objects.isNull(accountHoldersDTO.getUserId())) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account holders need at least one user.");
                } else {
                    //   Find user
                    UserDTO user = userRepository
                            .findById(accountHoldersDTO.getUserId())
                            .stream()
                            .map(userBD -> toDto(userBD))
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(user)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user was not founded.");
                    }
                    accountHoldersDTO.setPrimaryAddress(primaryAddress);
                    accountHoldersDTO.setUser(user);
                    AccountHoldersDTO resultDto = Optional.of(accountHoldersDTO)
                            .map(tDto -> toEntity(tDto))
                            .map(accountHoldersRepository::save)
                            .map(entity -> toDto(entity))
                            .orElse(null);
                    if (Objects.isNull(resultDto)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account holders was not created.");
                    }
                    return resultDto;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not created.");
        }
    }


    public AccountHoldersDTO update(long id, AccountHoldersDTO accountHoldersDTO) {
        try {
            if (Objects.isNull(accountHoldersDTO.getPrimaryAddressId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account holders need at least one address.");
            } else {
                //   Find the 1st Address
                AddressDTO primaryAddress = addressRepository
                        .findById(accountHoldersDTO.getPrimaryAddressId())
                        .stream()
                        .map(addressBD -> toDto(addressBD))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(primaryAddress)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The primary address was not founded.");
                }
                if (!Objects.isNull(accountHoldersDTO.getMailingAddressId())) {
                    //   Find the 2nd Address
                    AddressDTO mailingAddress = addressRepository
                            .findById(accountHoldersDTO.getMailingAddressId())
                            .stream()
                            .map(addressBD -> toDto(addressBD))
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(mailingAddress)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The mailing address was not founded.");
                    } else {
                        accountHoldersDTO.setMailingAddress(mailingAddress);
                    }
                }
                AccountHoldersDTO resultDto = accountHoldersRepository.findById(id)
                        .map(entity -> {
                            entity.setName(accountHoldersDTO.getName());
                            entity.setDateOfBirth(accountHoldersDTO.getDateOfBirth());
                            if (!Objects.isNull(accountHoldersDTO.getPrimaryAddress())) {
                                entity.setPrimaryAddress(
                                        Optional.of(accountHoldersDTO.getPrimaryAddress())
                                                .map(ahDto -> toEntity(ahDto)).get());
                            }
                            if (!Objects.isNull(accountHoldersDTO.getMailingAddress())) {
                                entity.setMailingAddress(
                                        Optional.of(accountHoldersDTO.getMailingAddress())
                                                .map(ahDto -> toEntity(ahDto)).get()
                                );
                            }
                            return accountHoldersRepository.save(entity);
                        })
                        .map(entity -> toDto(entity))
                        .orElse(null);
                if (Objects.isNull(resultDto)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account holders was not updated.");
                }
                return resultDto;
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account was not updated.");
        }
    }


    public void delete(Long id) {
        try {
            accountHoldersRepository.deleteById(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account holders was not deleted.");
        }
    }

}
