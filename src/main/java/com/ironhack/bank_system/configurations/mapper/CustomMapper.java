package com.ironhack.bank_system.configurations.mapper;

import org.modelmapper.ModelMapper;

public class CustomMapper extends ModelMapper {

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        Object tmpSource = source;
        if (source == null) {
            tmpSource = new Object();
        }
        return super.map(tmpSource, destinationType);
    }
}
