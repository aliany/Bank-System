package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.AccountDTO;
import com.ironhack.bank_system.dto.AccountHoldersDTO;
import com.ironhack.bank_system.dto.CheckingDTO;
import com.ironhack.bank_system.dto.StudentCheckingDTO;
import com.ironhack.bank_system.model.StudentChecking;
import com.ironhack.bank_system.services.AccountHoldersService;
import com.ironhack.bank_system.services.CheckingService;
import com.ironhack.bank_system.services.StudentCheckingService;
import com.ironhack.bank_system.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "checking")
@Api(value = "Checking Accounts Request")
public class CheckingController {

    private final CheckingService checkingService;
    private final StudentCheckingService studentCheckingService;
    private final AccountHoldersService accountHoldersService;

    @Autowired
    public CheckingController(
            CheckingService checkingService,
            StudentCheckingService studentCheckingService,
            AccountHoldersService accountHoldersService
    ) {
        this.checkingService = checkingService;
        this.studentCheckingService = studentCheckingService;
        this.accountHoldersService = accountHoldersService;
    }

    @GetMapping
    @ApiOperation(value = "Get all checking accounts", notes = "return all checking accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CheckingDTO> findAll() {
        return checkingService.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Search one checking account", notes = "return one checking account")
    @PreAuthorize("hasRole('ADMIN')")
    public CheckingDTO findAccountById(@PathVariable Long id) {
        return checkingService.findAccountById(id);
    }



    @PostMapping
    @ApiOperation(value = "Create one checking account", notes = "return one checking account")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckingDTO createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            if (Objects.isNull(accountDTO.getPrimaryOwnerId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account need at least one owner.");
            } else {
                //   Find the 1st Owner
                AccountHoldersDTO primaryOwnerDTO = accountHoldersService.findAccountHoldersById(accountDTO.getPrimaryOwnerId());
                if (Objects.isNull(primaryOwnerDTO)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The primary owner was not founded.");
                }
                if (Utils.getDiffYears(primaryOwnerDTO.getDateOfBirth(), new Date()) > 24) {
                    accountDTO.setPrimaryOwner(primaryOwnerDTO);
                    return checkingService.create(accountDTO);
                } else {
                    return studentCheckingService.createAsStudent(accountDTO);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The Checking account not created.");
        }
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updated one checking account", notes = "return one checking account")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTHOLDERS')")
    @ResponseStatus(HttpStatus.OK)
    public CheckingDTO updateAccount(@PathVariable Long id, @RequestBody @Valid CheckingDTO checkingDTO) {
        return checkingService.update(id, checkingDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete one checking account", notes = "Checking checking account")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(@PathVariable Long id) {
        checkingService.delete(id);
    }
}
