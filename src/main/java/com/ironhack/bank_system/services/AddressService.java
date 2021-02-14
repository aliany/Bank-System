package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.AddressDTO;
import com.ironhack.bank_system.dto.UserDTO;
import com.ironhack.bank_system.enums.Rol;
import com.ironhack.bank_system.model.Address;
import com.ironhack.bank_system.model.User;
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
public class AddressService {


    private AddressRepository addressRepository;
    private CustomMapper modelMapper;

    @Autowired
    public AddressService(
            AddressRepository addressRepository,
            CustomMapper modelMapper
    ) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    private AddressDTO toDto(Address address) {
        return modelMapper.map(address, AddressDTO.class);
    }

    private Address toEntity(AddressDTO addressDTO) throws ParseException {
        return modelMapper.map(addressDTO, Address.class);
    }


    public List<AddressDTO> findAll() {
        return this.addressRepository
                .findAll()
                .stream()
                .map(addressBD -> toDto(addressBD))
                .collect(Collectors.toList());
    }

    public AddressDTO findAddressById(Long id) {
        AddressDTO addressDTO = addressRepository.findById(id)
                .stream()
                .map(addressBD -> this.toDto(addressBD))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(addressDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The address was not found.");
        }
        return addressDTO;
    }

    public AddressDTO create(AddressDTO addressDTO) {
        try {
            AddressDTO resultDto = Optional.of(addressDTO)
                    .map(tDto -> toEntity(tDto))
                    .map(addressRepository::save)
                    .map(entity -> toDto(entity))
                    .orElse(null);
            if (Objects.isNull(resultDto)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The address was not created.");
            }
            return resultDto;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The address not created.");
        }

    }

    public AddressDTO update(Long id, AddressDTO addressDTO) {
        try {
            AddressDTO resultDto = addressRepository.findById(id)
                    .map(entity -> {
                        entity.setAddress(addressDTO.getAddress());
                        return addressRepository.save(entity);
                    })
                    .map(entity -> toDto(entity))
                    .orElse(null);
            if (Objects.isNull(resultDto)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The address was not updated.");
            }
            return resultDto;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The address was not updated.");
        }
    }

    public void delete(Long id) {
        try {
            addressRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The address was not deleted.");
        }
    }
}
