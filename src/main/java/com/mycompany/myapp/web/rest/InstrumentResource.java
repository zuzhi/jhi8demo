package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.InstrumentRepository;
import com.mycompany.myapp.service.InstrumentService;
import com.mycompany.myapp.service.dto.InstrumentDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Instrument}.
 */
@RestController
@RequestMapping("/api/instruments")
public class InstrumentResource {

    private final Logger log = LoggerFactory.getLogger(InstrumentResource.class);

    private static final String ENTITY_NAME = "instrument";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstrumentService instrumentService;

    private final InstrumentRepository instrumentRepository;

    public InstrumentResource(InstrumentService instrumentService, InstrumentRepository instrumentRepository) {
        this.instrumentService = instrumentService;
        this.instrumentRepository = instrumentRepository;
    }

    /**
     * {@code POST  /instruments} : Create a new instrument.
     *
     * @param instrumentDTO the instrumentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instrumentDTO, or with status {@code 400 (Bad Request)} if the instrument has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InstrumentDTO> createInstrument(@RequestBody InstrumentDTO instrumentDTO) throws URISyntaxException {
        log.debug("REST request to save Instrument : {}", instrumentDTO);
        if (instrumentDTO.getId() != null) {
            throw new BadRequestAlertException("A new instrument cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InstrumentDTO result = instrumentService.save(instrumentDTO);
        return ResponseEntity
            .created(new URI("/api/instruments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /instruments/:id} : Updates an existing instrument.
     *
     * @param id the id of the instrumentDTO to save.
     * @param instrumentDTO the instrumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instrumentDTO,
     * or with status {@code 400 (Bad Request)} if the instrumentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instrumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstrumentDTO> updateInstrument(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody InstrumentDTO instrumentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Instrument : {}, {}", id, instrumentDTO);
        if (instrumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instrumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instrumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        InstrumentDTO result = instrumentService.update(instrumentDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, instrumentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /instruments/:id} : Partial updates given fields of an existing instrument, field will ignore if it is null
     *
     * @param id the id of the instrumentDTO to save.
     * @param instrumentDTO the instrumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instrumentDTO,
     * or with status {@code 400 (Bad Request)} if the instrumentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the instrumentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the instrumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InstrumentDTO> partialUpdateInstrument(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody InstrumentDTO instrumentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Instrument partially : {}, {}", id, instrumentDTO);
        if (instrumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instrumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instrumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InstrumentDTO> result = instrumentService.partialUpdate(instrumentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, instrumentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /instruments} : get all the instruments.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instruments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InstrumentDTO>> getAllInstruments(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Instruments");
        Page<InstrumentDTO> page = instrumentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /instruments/:id} : get the "id" instrument.
     *
     * @param id the id of the instrumentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instrumentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstrumentDTO> getInstrument(@PathVariable("id") Long id) {
        log.debug("REST request to get Instrument : {}", id);
        Optional<InstrumentDTO> instrumentDTO = instrumentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(instrumentDTO);
    }

    /**
     * {@code DELETE  /instruments/:id} : delete the "id" instrument.
     *
     * @param id the id of the instrumentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstrument(@PathVariable("id") Long id) {
        log.debug("REST request to delete Instrument : {}", id);
        instrumentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
