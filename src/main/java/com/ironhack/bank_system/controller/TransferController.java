package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.TransferDTO;
import com.ironhack.bank_system.dto.UserDTO;
import com.ironhack.bank_system.model.Transfer;
import com.ironhack.bank_system.services.TransferService;
import com.ironhack.bank_system.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "transfer")
@Api(value = "Transfer Request")
public class TransferController {

    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping
    @ApiOperation(value = "Get all transfers", notes = "return all transfers")
    @PreAuthorize("hasAnyRole('ACCOUNTHOLDERS')")
    public List<TransferDTO> findAll() {
        return transferService.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Search one transfer", notes = "return one transfer")
    @PreAuthorize("hasAnyRole('ACCOUNTHOLDERS')")
    public TransferDTO findUserById(@PathVariable Long id) {
        return transferService.findUserById(id);
    }

    @PostMapping
    @ApiOperation(value = "Create one transfer", notes = "return one transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferDTO create(
            @Valid @RequestBody TransferDTO transferDTO
    ) {
        return transferService.createTransfer(transferDTO);
    }

}
