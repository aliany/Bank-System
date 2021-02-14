package com.ironhack.bank_system.services;

import com.ironhack.bank_system.configurations.mapper.CustomMapper;
import com.ironhack.bank_system.dto.ThirdPartyTransferDTO;
import com.ironhack.bank_system.dto.TransferDTO;
import com.ironhack.bank_system.enums.Status;
import com.ironhack.bank_system.enums.TransferType;
import com.ironhack.bank_system.model.Account;
import com.ironhack.bank_system.model.IMaxAmount;
import com.ironhack.bank_system.model.Transfer;
import com.ironhack.bank_system.repository.AccountRepository;
import com.ironhack.bank_system.repository.TransferRepository;
import com.ironhack.bank_system.utils.Utils;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.money.Monetary;
import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class TransferService extends BaseService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final CustomMapper modelMapper;

    @Autowired
    public TransferService(
            TransferRepository transferRepository,
            AccountRepository accountRepository,
            CustomMapper modelMapper
    ) {
        super(modelMapper);
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    public List<TransferDTO> findAll() {
        return this.transferRepository
                .findAll()
                .stream()
                .map(transferBD -> toDto(transferBD))
                .collect(Collectors.toList());
    }

    public TransferDTO findUserById(Long id) {
        TransferDTO transferDTO = transferRepository.findById(id)
                .stream()
                .map(transferBD -> toDto(transferBD))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(transferDTO)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The account admin was not found.");
        }
        return transferDTO;
    }

    public List<Transfer> findByAccountToAndType(Long id, TransferType rate) {
        return transferRepository.findByAccountToAndType(id, rate);
    }

    public TransferDTO createTransfer(TransferDTO transferDTO) {
        try {
            Account accountFrom = accountRepository.findByIban(transferDTO.getIbanFrom()).orElse(null);
            if (Objects.isNull(accountFrom) || !accountFrom.getStatus().equals(Status.ACTIVE)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The transfer cannot be made.");
            }
            Account accountTo = accountRepository.findByIbanAndPrimaryOwnerName(transferDTO.getIbanTo(), transferDTO.getAccountToName()).orElse(null);
            if (Objects.isNull(accountTo) || !accountTo.getStatus().equals(Status.ACTIVE)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The transfer cannot be made.");
            }
            if (
                    accountFrom.getBalance().isGreaterThanOrEqualTo(
                            Money.of(transferDTO.getAmountQty(), Monetary.getCurrency("EUR"))
                    )
            ) {
                accountFrom.setBalance(
                        accountFrom.getBalance()
                                .subtract(Money.of(transferDTO.getAmountQty(), Monetary.getCurrency("EUR")))
                );
                accountTo.setBalance(
                        accountTo.getBalance()
                                .add(Money.of(transferDTO.getAmountQty(), Monetary.getCurrency("EUR")))
                );
                transferDTO.setAccountFrom(
                        Optional.of(accountFrom)
                                .map(accountDB -> toDto(accountDB))
                                .get()
                );
                transferDTO.setAccountTo(
                        Optional.of(accountTo)
                                .map(accountDB -> toDto(accountDB))
                                .get()
                );
                if (verifyFraudDetection(transferDTO)) {
                    return create(transferDTO);
                } else {
                    accountFrom.setStatus(Status.FROZEN);
                    accountRepository.save(accountFrom);
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Sorry! NOT_ACCEPTABLE.");
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! Insufficient funds.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The transfer cannot be made.");
        }
    }

    public void createExternalTransfer(ThirdPartyTransferDTO thirdPartyTransferDTO) {
        try {
            Account accountFrom = accountRepository.findBySecretKey(thirdPartyTransferDTO.getAccountFromSecretKey()).orElse(null);
            if (Objects.isNull(accountFrom) || !accountFrom.getStatus().equals(Status.ACTIVE)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The transfer cannot be made.");
            }
            Account accountTo = accountRepository.findById(thirdPartyTransferDTO.getAccountToId()).orElse(null);
            if (Objects.isNull(accountTo) || !accountTo.getStatus().equals(Status.ACTIVE)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The transfer cannot be made.");
            }
            if (
                    accountFrom.getBalance().isGreaterThanOrEqualTo(
                            Money.of(thirdPartyTransferDTO.getAmountQty(), Monetary.getCurrency("EUR"))
                    )
            ) {
                accountFrom.setBalance(
                        accountFrom.getBalance()
                                .subtract(Money.of(thirdPartyTransferDTO.getAmountQty(), Monetary.getCurrency("EUR")))
                );
                accountTo.setBalance(
                        accountTo.getBalance()
                                .add(Money.of(thirdPartyTransferDTO.getAmountQty(), Monetary.getCurrency("EUR")))
                );
                TransferDTO transferDTOToSave = TransferDTO
                        .builder()
                        .accountFrom(
                                Optional.of(accountFrom)
                                        .map(accountDB -> toDto(accountDB))
                                        .get()
                        )
                        .accountTo(
                                Optional.of(accountTo)
                                        .map(accountDB -> toDto(accountDB))
                                        .get()
                        )
                        .accountFromName(accountFrom.getPrimaryOwner().getName())
                        .amountQty(thirdPartyTransferDTO.getAmountQty())
                        .date(new Date())
                        .concept("External Transfer!")
                        .type(TransferType.EXTERNAL)
                        .build();
                if (verifyFraudDetection(transferDTOToSave)) {
                    create(transferDTOToSave);
                } else {
                    accountFrom.setStatus(Status.FROZEN);
                    accountRepository.save(accountFrom);
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Sorry! Insufficient funds.");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! Insufficient funds.");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The transfer cannot be made.");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public TransferDTO create(TransferDTO transferDTO) {
        try {
            TransferDTO resultDto = Optional.of(transferDTO)
                    .map(tDto -> toEntity(tDto))
                    .map(transferRepository::save)
                    .map(entity -> {
                        Account accountFromDB = accountRepository.findByIban(entity.getAccountFrom().getIban()).orElse(null);
                        entity.setAccountFrom(accountFromDB);
                        return toDto(entity);
                    })
                    .orElse(null);
            if (Objects.isNull(resultDto)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The transfer cannot be made.");
            }
            return resultDto;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! The transfer cannot be made.");
        }
    }

    private boolean verifyFraudDetection(TransferDTO transferDTO) {
        AtomicBoolean result = new AtomicBoolean(true);
        DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String transferDate = Utils.convertToLocalDateViaInstant(transferDTO.getDate()).format(newPattern);

        Optional<IMaxAmount> sumAmount = transferRepository.findMaxAmountInDateFromAccount(transferDate, transferDTO.getAccountFrom().getId());
        Double totalDay = transferDTO.getAmountQty();

        Double totalMaxDay = Double.valueOf(0);
        Date compareDate = Utils.getDateAfterSum(transferDTO.getDate(), -7, Calendar.DAY_OF_WEEK);
        String compareDateStr = Utils.convertToLocalDateViaInstant(compareDate).format(newPattern);
        Optional<IMaxAmount> maxAmount = transferRepository.findMaxAmountInDate(compareDateStr);

        if (sumAmount.isPresent()) {
            totalDay += sumAmount.get().getMaxAmount();
        }
        if (maxAmount.isPresent()) {
            totalMaxDay += maxAmount.get().getMaxAmount();
            if (totalDay > (totalMaxDay * 1.5)) {
                result.set(false);
            }
        }

        Optional<Transfer> lastTransferAccountFrom = transferRepository.findByAccountFromOrderByCreatedDateDesc(
                Optional.of(transferDTO.getAccountFrom())
                        .map(accountDB -> toEntity(accountDB))
                        .get()
        ).stream().findFirst();
        if (lastTransferAccountFrom.isPresent()) {
            long seconds = (transferDTO.getDate().getTime() - lastTransferAccountFrom.get().getCreatedDate().getTime()) / 1000;
            if (seconds <= 1) {
                result.set(false);
            }
        }

        return result.get();
    }


}
