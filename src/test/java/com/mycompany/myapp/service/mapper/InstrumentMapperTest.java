package com.mycompany.myapp.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class InstrumentMapperTest {

    private InstrumentMapper instrumentMapper;

    @BeforeEach
    public void setUp() {
        instrumentMapper = new InstrumentMapperImpl();
    }
}
