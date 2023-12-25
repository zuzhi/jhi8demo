package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Instrument;
import com.mycompany.myapp.repository.InstrumentRepository;
import com.mycompany.myapp.service.dto.InstrumentDTO;
import com.mycompany.myapp.service.mapper.InstrumentMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InstrumentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InstrumentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/instruments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private InstrumentMapper instrumentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInstrumentMockMvc;

    private Instrument instrument;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instrument createEntity(EntityManager em) {
        Instrument instrument = new Instrument().name(DEFAULT_NAME);
        return instrument;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instrument createUpdatedEntity(EntityManager em) {
        Instrument instrument = new Instrument().name(UPDATED_NAME);
        return instrument;
    }

    @BeforeEach
    public void initTest() {
        instrument = createEntity(em);
    }

    @Test
    @Transactional
    void createInstrument() throws Exception {
        int databaseSizeBeforeCreate = instrumentRepository.findAll().size();
        // Create the Instrument
        InstrumentDTO instrumentDTO = instrumentMapper.toDto(instrument);
        restInstrumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instrumentDTO)))
            .andExpect(status().isCreated());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeCreate + 1);
        Instrument testInstrument = instrumentList.get(instrumentList.size() - 1);
        assertThat(testInstrument.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createInstrumentWithExistingId() throws Exception {
        // Create the Instrument with an existing ID
        instrument.setId(1L);
        InstrumentDTO instrumentDTO = instrumentMapper.toDto(instrument);

        int databaseSizeBeforeCreate = instrumentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInstrumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instrumentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInstruments() throws Exception {
        // Initialize the database
        instrumentRepository.saveAndFlush(instrument);

        // Get all the instrumentList
        restInstrumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instrument.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getInstrument() throws Exception {
        // Initialize the database
        instrumentRepository.saveAndFlush(instrument);

        // Get the instrument
        restInstrumentMockMvc
            .perform(get(ENTITY_API_URL_ID, instrument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(instrument.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingInstrument() throws Exception {
        // Get the instrument
        restInstrumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstrument() throws Exception {
        // Initialize the database
        instrumentRepository.saveAndFlush(instrument);

        int databaseSizeBeforeUpdate = instrumentRepository.findAll().size();

        // Update the instrument
        Instrument updatedInstrument = instrumentRepository.findById(instrument.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInstrument are not directly saved in db
        em.detach(updatedInstrument);
        updatedInstrument.name(UPDATED_NAME);
        InstrumentDTO instrumentDTO = instrumentMapper.toDto(updatedInstrument);

        restInstrumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instrumentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instrumentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeUpdate);
        Instrument testInstrument = instrumentList.get(instrumentList.size() - 1);
        assertThat(testInstrument.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingInstrument() throws Exception {
        int databaseSizeBeforeUpdate = instrumentRepository.findAll().size();
        instrument.setId(longCount.incrementAndGet());

        // Create the Instrument
        InstrumentDTO instrumentDTO = instrumentMapper.toDto(instrument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstrumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instrumentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instrumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstrument() throws Exception {
        int databaseSizeBeforeUpdate = instrumentRepository.findAll().size();
        instrument.setId(longCount.incrementAndGet());

        // Create the Instrument
        InstrumentDTO instrumentDTO = instrumentMapper.toDto(instrument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstrumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instrumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstrument() throws Exception {
        int databaseSizeBeforeUpdate = instrumentRepository.findAll().size();
        instrument.setId(longCount.incrementAndGet());

        // Create the Instrument
        InstrumentDTO instrumentDTO = instrumentMapper.toDto(instrument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstrumentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instrumentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInstrumentWithPatch() throws Exception {
        // Initialize the database
        instrumentRepository.saveAndFlush(instrument);

        int databaseSizeBeforeUpdate = instrumentRepository.findAll().size();

        // Update the instrument using partial update
        Instrument partialUpdatedInstrument = new Instrument();
        partialUpdatedInstrument.setId(instrument.getId());

        partialUpdatedInstrument.name(UPDATED_NAME);

        restInstrumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstrument.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInstrument))
            )
            .andExpect(status().isOk());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeUpdate);
        Instrument testInstrument = instrumentList.get(instrumentList.size() - 1);
        assertThat(testInstrument.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateInstrumentWithPatch() throws Exception {
        // Initialize the database
        instrumentRepository.saveAndFlush(instrument);

        int databaseSizeBeforeUpdate = instrumentRepository.findAll().size();

        // Update the instrument using partial update
        Instrument partialUpdatedInstrument = new Instrument();
        partialUpdatedInstrument.setId(instrument.getId());

        partialUpdatedInstrument.name(UPDATED_NAME);

        restInstrumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstrument.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInstrument))
            )
            .andExpect(status().isOk());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeUpdate);
        Instrument testInstrument = instrumentList.get(instrumentList.size() - 1);
        assertThat(testInstrument.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingInstrument() throws Exception {
        int databaseSizeBeforeUpdate = instrumentRepository.findAll().size();
        instrument.setId(longCount.incrementAndGet());

        // Create the Instrument
        InstrumentDTO instrumentDTO = instrumentMapper.toDto(instrument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstrumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, instrumentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(instrumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstrument() throws Exception {
        int databaseSizeBeforeUpdate = instrumentRepository.findAll().size();
        instrument.setId(longCount.incrementAndGet());

        // Create the Instrument
        InstrumentDTO instrumentDTO = instrumentMapper.toDto(instrument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstrumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(instrumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstrument() throws Exception {
        int databaseSizeBeforeUpdate = instrumentRepository.findAll().size();
        instrument.setId(longCount.incrementAndGet());

        // Create the Instrument
        InstrumentDTO instrumentDTO = instrumentMapper.toDto(instrument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstrumentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(instrumentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instrument in the database
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInstrument() throws Exception {
        // Initialize the database
        instrumentRepository.saveAndFlush(instrument);

        int databaseSizeBeforeDelete = instrumentRepository.findAll().size();

        // Delete the instrument
        restInstrumentMockMvc
            .perform(delete(ENTITY_API_URL_ID, instrument.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Instrument> instrumentList = instrumentRepository.findAll();
        assertThat(instrumentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
