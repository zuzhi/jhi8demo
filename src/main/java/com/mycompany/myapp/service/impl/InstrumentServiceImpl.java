package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Instrument;
import com.mycompany.myapp.repository.InstrumentRepository;
import com.mycompany.myapp.service.InstrumentService;
import com.mycompany.myapp.service.dto.InstrumentDTO;
import com.mycompany.myapp.service.mapper.InstrumentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Instrument}.
 */
@Service
@Transactional
public class InstrumentServiceImpl implements InstrumentService {

    private final Logger log = LoggerFactory.getLogger(InstrumentServiceImpl.class);

    private final InstrumentRepository instrumentRepository;

    private final InstrumentMapper instrumentMapper;

    public InstrumentServiceImpl(InstrumentRepository instrumentRepository, InstrumentMapper instrumentMapper) {
        this.instrumentRepository = instrumentRepository;
        this.instrumentMapper = instrumentMapper;
    }

    @Override
    public InstrumentDTO save(InstrumentDTO instrumentDTO) {
        log.debug("Request to save Instrument : {}", instrumentDTO);
        Instrument instrument = instrumentMapper.toEntity(instrumentDTO);
        instrument = instrumentRepository.save(instrument);
        return instrumentMapper.toDto(instrument);
    }

    @Override
    public InstrumentDTO update(InstrumentDTO instrumentDTO) {
        log.debug("Request to update Instrument : {}", instrumentDTO);
        Instrument instrument = instrumentMapper.toEntity(instrumentDTO);
        instrument = instrumentRepository.save(instrument);
        return instrumentMapper.toDto(instrument);
    }

    @Override
    public Optional<InstrumentDTO> partialUpdate(InstrumentDTO instrumentDTO) {
        log.debug("Request to partially update Instrument : {}", instrumentDTO);

        return instrumentRepository
            .findById(instrumentDTO.getId())
            .map(existingInstrument -> {
                instrumentMapper.partialUpdate(existingInstrument, instrumentDTO);

                return existingInstrument;
            })
            .map(instrumentRepository::save)
            .map(instrumentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstrumentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Instruments");
        return instrumentRepository.findAll(pageable).map(instrumentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InstrumentDTO> findOne(Long id) {
        log.debug("Request to get Instrument : {}", id);
        return instrumentRepository.findById(id).map(instrumentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Instrument : {}", id);
        instrumentRepository.deleteById(id);
    }
}
