package com.ironhack.bank_system.controller;

import com.ironhack.bank_system.dto.AddressDTO;
import com.ironhack.bank_system.dto.UserDTO;
import com.ironhack.bank_system.services.AddressService;
import com.ironhack.bank_system.services.UserService;
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
@RequestMapping(value = "address")
@Api(value = "Address Request")
public class AddressController {

  @Autowired
  private AddressService addressService;

  @GetMapping
  @ApiOperation(value = "Get all addresses", notes = "return all addresses")
  @PreAuthorize("hasRole('ADMIN')")
  public List<AddressDTO> findAll() {
    return addressService.findAll();
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Search one address", notes = "return one address")
  @PreAuthorize("hasRole('ADMIN')")
  public AddressDTO findAddressById(@PathVariable Long id) {
    return addressService.findAddressById(id);
  }

  @PostMapping
  @ApiOperation(value = "Create one address", notes = "return one address")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public AddressDTO createAddress(@Valid @RequestBody AddressDTO addressDTO) {
    return addressService.create(addressDTO);
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Updated one address", notes = "return one address")
  @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTHOLDERS')")
  @ResponseStatus(HttpStatus.OK)
  public AddressDTO updateAddress(@PathVariable Long id, @RequestBody @Valid AddressDTO addressDTO) {
    return addressService.update(id, addressDTO);
  }

  @DeleteMapping("/{id}")
  @ApiOperation(value = "Delete one address", notes = "Address deleted")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  public void deleteAddress(@PathVariable Long id) {
    addressService.delete(id);
  }

}
