package com.ironhack.bank_system.repository;

import com.ironhack.bank_system.model.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingRepository extends JpaRepository <Saving, Long>{
}
