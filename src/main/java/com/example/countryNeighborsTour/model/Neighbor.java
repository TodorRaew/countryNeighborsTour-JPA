package com.example.countryNeighborsTour.model;

import javax.persistence.*;

@Entity(name = "Neighbor")
@Table(name = "neighbor")
public class Neighbor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "starting_country_name",
            nullable = false,
            columnDefinition = "TEXT")
    private String startingCountryName;

    @Column(name = "neighbor_country_name",
            nullable = false,
            columnDefinition = "TEXT")
    private String neighborCountryName;

    public Neighbor() {
    }

    public Neighbor(String startingCountryName, String neighborCountryName) {
        this.startingCountryName = startingCountryName;
        this.neighborCountryName = neighborCountryName;
    }

    public Long getId() {
        return id;
    }

    public String getStartingCountryName() {
        return startingCountryName;
    }

    public void setStartingCountryName(String startingCountryName) {
        this.startingCountryName = startingCountryName;
    }

    public String getNeighborCountryName() {
        return neighborCountryName;
    }

    public void setNeighborCountryName(String neighborCountryName) {
        this.neighborCountryName = neighborCountryName;
    }
}