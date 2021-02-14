package com.ironhack.bank_system.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class StudentCheckingDTO extends AccountDTO {

    private String secretKey;
    @Builder.Default
    private Date creationDate = new Date();
}
