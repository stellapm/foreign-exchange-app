package com.fea.foreign.exchange.app.model.mapper;

import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.entity.CurrencyConversion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CurrencyConversionMapper {
    CurrencyConversionMapper INSTANCE = Mappers.getMapper(CurrencyConversionMapper.class);


    CurrencyConversion conversionRequestDTOtoCurrencyConversion(CurrencyConversionRequestDTO conversionRequestDTO);
}
