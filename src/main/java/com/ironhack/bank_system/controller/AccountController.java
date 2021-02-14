package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.AccountDTO;
import com.ironhack.bank_system.model.Account;
import com.ironhack.bank_system.services.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import com.ironhack.bank_system.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "account")
@Api(value = "Account´s Request")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @ApiOperation(value = "Get all accounts", notes = "return all accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountDTO> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Search one account", notes = "return one account")
    @PreAuthorize("hasRole('ADMIN')")
    public AccountDTO findAccountById(@PathVariable Long id) {
        return accountService.findAccountById(id);
    }


    @PostMapping
    @ApiOperation(value = "Create one account", notes = "return one account")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDTO createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        return accountService.create(accountDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updated one account", notes = "return one account")
    @PreAuthorize("hasAnyRole('ACCOUNTHOLDERS', 'ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public AccountDTO updateAccount(@PathVariable Long id, @RequestBody @Valid AccountDTO accountDTO) {
        return accountService.update(id, accountDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete one account", notes = "Account deleted")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@PathVariable Long id) {
        accountService.delete(id);
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('ACCOUNTHOLDERS', 'ADMIN')")
    @ApiOperation(value = "Find the balance of an account by IBAN", notes = "Returns the account balance")
    public Object getBalance(@NotNull @RequestParam(name = "iban") String iban) {
        if (iban.length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The balance cannot be get.");
        }
        return accountService.getBalance(iban);
    }
}
