package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.AddressDTO;
import com.ironhack.bank_system.dto.ThirdPartyTransferDTO;
import com.ironhack.bank_system.dto.TransferDTO;
import com.ironhack.bank_system.services.AddressService;
import com.ironhack.bank_system.services.TransferService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "external")
public class ThirdPartyController {

    @Autowired
    private TransferService transferService;

    @PostMapping("transfer")
    @ApiOperation(value = "Create one transfer", notes = "return one transfer")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String create(
            @Valid @RequestBody ThirdPartyTransferDTO thirdPartyTransferDTO
    ) {
        transferService.createExternalTransfer(thirdPartyTransferDTO);
        return "SUCCESS!";
    }

}
