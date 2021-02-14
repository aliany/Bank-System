package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.AccountDTO;
import com.ironhack.bank_system.dto.AccountHoldersDTO;
import com.ironhack.bank_system.services.AccountHoldersService;
import com.ironhack.bank_system.services.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/account/holders")
@Api(value = "Account Holder Request")
public class AccountHoldersController {

  private final AccountHoldersService accountHoldersService;

  @Autowired
  public AccountHoldersController(AccountHoldersService accountHoldersService) {
    this.accountHoldersService = accountHoldersService;
  }

  @GetMapping
  @ApiOperation(value = "Get all account holder", notes = "return all account holder")
  @PreAuthorize("hasRole('ADMIN')")
  public List<AccountHoldersDTO> findAll() {
    return accountHoldersService.findAll();
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Search one account holder", notes = "return one account holder")
  @PreAuthorize("hasRole('ADMIN')")
  public AccountHoldersDTO findAccountHoldersById(@PathVariable Long id) {
    return accountHoldersService.findAccountHoldersById(id);
  }

  @PostMapping
  @ApiOperation(value = "Create one account holder", notes = "return one account holder")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public AccountHoldersDTO createAccountHolders(@Valid @RequestBody AccountHoldersDTO accountHoldersDTO) {
    return accountHoldersService.create(accountHoldersDTO);
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Updated one account holder", notes = "return one account holder")
  @PreAuthorize("hasAnyRole('ACCOUNTHOLDERS', 'ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public AccountHoldersDTO updateAccountHolders(@PathVariable Long id, @RequestBody @Valid AccountHoldersDTO accountHoldersDTO) {
    return accountHoldersService.update(id, accountHoldersDTO);
  }

  @DeleteMapping("/{id}")
  @ApiOperation(value = "Delete one account holder", notes = "Account holder deleted")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public void deleteAccountHolders(@PathVariable Long id) {
    accountHoldersService.delete(id);
  }


}
