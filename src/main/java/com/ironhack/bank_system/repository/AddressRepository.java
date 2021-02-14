package com.ironhack.bank_system.repository;

import com.ironhack.bank_system.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AddressRepository extends JpaRepository <Address, Long> {
}
