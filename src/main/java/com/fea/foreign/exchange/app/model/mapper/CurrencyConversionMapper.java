package com.fea.foreign.exchange.app.model.mapper;

import com.fea.foreign.exchange.app.model.dto.CurrencyConversionRequestDTO;
import com.fea.foreign.exchange.app.model.entity.CurrencyConversion;
import com.fea.foreign.exchange.app.model.view.CurrencyConversionInfoView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public interface CurrencyConversionMapper {
    CurrencyConversionMapper INSTANCE = Mappers.getMapper(CurrencyConversionMapper.class);


    CurrencyConversion conversionRequestDTOtoCurrencyConversion(CurrencyConversionRequestDTO conversionRequestDTO);

    @Mapping(target = "transactionDate", expression = "java( mapToLocalDate(conversion.getTransactionTimeStamp()) )")
    CurrencyConversionInfoView conversionToConversionInfoView(CurrencyConversion conversion);

    default LocalDate mapToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }
}
