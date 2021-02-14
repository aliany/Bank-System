package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.SavingDTO;
import com.ironhack.bank_system.dto.StudentCheckingDTO;
import com.ironhack.bank_system.services.SavingService;
import com.ironhack.bank_system.services.StudentCheckingService;
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
@RequestMapping(value = "saving")
@Api(value = "Savings Accounts Request")
public class SavingController {

    private final SavingService savingService;

    @Autowired
    public SavingController(SavingService savingService) {
        this.savingService = savingService;
    }

    @GetMapping
    @ApiOperation(value = "Get all savings accounts", notes = "return all savings accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SavingDTO> findAll() {
        return savingService.findAll();
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "Search one savings account", notes = "return one savings account")
    @PreAuthorize("hasRole('ADMIN')")
    public SavingDTO findAccountById(@PathVariable Long id) {
        return savingService.findAccountById(id);
    }


    @PostMapping
    @ApiOperation(value = "Create one savings account", notes = "return one savings account")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingDTO createAccount(@Valid @RequestBody SavingDTO savingDTO) {
        return savingService.create(savingDTO);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updated one savings account", notes = "return one savings account")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTHOLDERS', 'THIRDPARTY')")
    @ResponseStatus(HttpStatus.OK)
    public SavingDTO updateAccount(@PathVariable Long id, @RequestBody @Valid SavingDTO savingDTO) {
        return savingService.update(id, savingDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete one savings account", notes = "Savings account deleted")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@PathVariable Long id) {
        savingService.delete(id);
    }
}
