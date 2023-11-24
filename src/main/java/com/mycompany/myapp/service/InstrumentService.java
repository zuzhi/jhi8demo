package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.InstrumentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Instrument}.
 */
public interface InstrumentService {
    /**
     * Save a instrument.
     *
     * @param instrumentDTO the entity to save.
     * @return the persisted entity.
     */
    InstrumentDTO save(InstrumentDTO instrumentDTO);

    /**
     * Updates a instrument.
     *
     * @param instrumentDTO the entity to update.
     * @return the persisted entity.
     */
    InstrumentDTO update(InstrumentDTO instrumentDTO);

    /**
     * Partially updates a instrument.
     *
     * @param instrumentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InstrumentDTO> partialUpdate(InstrumentDTO instrumentDTO);

    /**
     * Get all the instruments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InstrumentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" instrument.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InstrumentDTO> findOne(Long id);

    /**
     * Delete the "id" instrument.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
