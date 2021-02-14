package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.CheckingDTO;
import com.ironhack.bank_system.dto.CreditCardDTO;
import com.ironhack.bank_system.services.CheckingService;
import com.ironhack.bank_system.services.CreditCardService;
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
@RequestMapping(value = "credit-card")
@Api(value = "Credit Card Accounts Request")
public class CreditCardController {
    private final CreditCardService creditCardService;

    @Autowired
    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @GetMapping
    @ApiOperation(value = "Get all credit card accounts", notes = "return all credit card accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CreditCardDTO> findAll() {
        return creditCardService.findAll();
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "Search one credit card account", notes = "return one credit card account")
    @PreAuthorize("hasRole('ADMIN')")
    public CreditCardDTO findAccountById(@PathVariable Long id) {
        return creditCardService.findAccountById(id);
    }

    @PostMapping
    @ApiOperation(value = "Create one credit card account", notes = "return one credit card account")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardDTO createAccount(@Valid @RequestBody CreditCardDTO creditCardDTO) {
        return creditCardService.create(creditCardDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updated one credit card account", notes = "return one credit card account")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTHOLDERS', 'THIRDPARTY')")
    @ResponseStatus(HttpStatus.OK)
    public CreditCardDTO updateAccount(@PathVariable Long id, @RequestBody @Valid CreditCardDTO creditCardDTO) {
        return creditCardService.update(id, creditCardDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete one credit card account", notes = "Credit card account deleted")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@PathVariable Long id) {
        creditCardService.delete(id);
    }
}
