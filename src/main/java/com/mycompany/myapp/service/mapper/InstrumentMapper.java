package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Instrument;
import com.mycompany.myapp.service.dto.InstrumentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Instrument} and its DTO {@link InstrumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstrumentMapper extends EntityMapper<InstrumentDTO, Instrument> {}
