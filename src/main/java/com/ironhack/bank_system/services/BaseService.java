package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.*;
import com.ironhack.bank_system.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.expression.ParseException;

public class BaseService {

    private final CustomMapper modelMapper;

    public BaseService(CustomMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    //    Account
    public AccountDTO toDto(Account account) {
        return modelMapper.map(account, AccountDTO.class);
    }

    public Account toEntity(AccountDTO accountDto) throws ParseException {
        return modelMapper.map(accountDto, Account.class);
    }

    //    Checking
    public CheckingDTO toDto(Checking checking) {
        return modelMapper.map(checking, CheckingDTO.class);
    }

    public Checking toEntity(CheckingDTO checkingDto) throws ParseException {
        return modelMapper.map(checkingDto, Checking.class);
    }

    public CheckingDTO AccountDTOtoCheckingDTO(AccountDTO accountDTO) throws ParseException {
        return modelMapper.map(accountDTO, CheckingDTO.class);
    }

    //    StudentChecking
    public StudentCheckingDTO toDto(StudentChecking studentChecking) {
        return modelMapper.map(studentChecking, StudentCheckingDTO.class);
    }

    public StudentChecking toEntity(StudentCheckingDTO studentCheckingDTO) throws ParseException {
        return modelMapper.map(studentCheckingDTO, StudentChecking.class);
    }

    public CheckingDTO StudentCheckingDTOtoCheckingDTO(StudentCheckingDTO studentCheckingDTO) throws ParseException {
        return modelMapper.map(studentCheckingDTO, CheckingDTO.class);
    }

    public StudentCheckingDTO AccountDTOtoStudentCheckingDTO(AccountDTO accountDTO) throws ParseException {
        return modelMapper.map(accountDTO, StudentCheckingDTO.class);
    }

    //    Saving
    public SavingDTO toDto(Saving saving) {
        return modelMapper.map(saving, SavingDTO.class);
    }

    public Saving toEntity(SavingDTO savingDTO) throws ParseException {
        return modelMapper.map(savingDTO, Saving.class);
    }

    //    CreditCard
    public CreditCardDTO toDto(CreditCard creditCard) {
        return modelMapper.map(creditCard, CreditCardDTO.class);
    }

    public CreditCard toEntity(CreditCardDTO creditCardDTO) throws ParseException {
        return modelMapper.map(creditCardDTO, CreditCard.class);
    }

    //    AccountHolders
    public AccountHoldersDTO toDto(AccountHolders accountHolders) {
        return modelMapper.map(accountHolders, AccountHoldersDTO.class);
    }

    public AccountHolders toEntity(AccountHoldersDTO accountHoldersDTO) throws ParseException {
        return modelMapper.map(accountHoldersDTO, AccountHolders.class);
    }

    //    Address
    public AddressDTO toDto(Address address) {
        return modelMapper.map(address, AddressDTO.class);
    }

    public Address toEntity(AddressDTO addressDTO) throws ParseException {
        return modelMapper.map(addressDTO, Address.class);
    }

    //    User
    public UserDTO toDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User toEntity(UserDTO userDTO) throws ParseException {
        return modelMapper.map(userDTO, User.class);
    }

    //    Transfer
    public TransferDTO toDto(Transfer transfer) {
        return modelMapper.map(transfer, TransferDTO.class);
    }

    public Transfer toEntity(TransferDTO transferDTO) throws ParseException {
        return modelMapper.map(transferDTO, Transfer.class);
    }
}
