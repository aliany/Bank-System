package com.ironhack.bank_system.repository;

import com.ironhack.bank_system.model.AccountHolders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountHoldersRepository extends JpaRepository <AccountHolders, Long> {
}
