package com.mycompany.myapp.application.domain.model;

public class InstrumentModel {

    private final Long id;
    private final String name;

    public InstrumentModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
