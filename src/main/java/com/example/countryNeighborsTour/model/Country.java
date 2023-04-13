package com.example.countryNeighborsTour.model;

import javax.persistence.*;

@Entity(name = "Country")
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "country_name",
            nullable = false,
            columnDefinition = "TEXT")
    private String countryName;

    @Column(name = "currency_abrev",
            nullable = false,
            columnDefinition = "TEXT")
    private String currencyAbrev;

    public Country() {
    }

    public Country(String countryName, String currencyAbrev) {
        this.countryName = countryName;
        this.currencyAbrev = currencyAbrev;
    }

    public Long getCountryId() {
        return countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCurrencyAbrev() {
        return currencyAbrev;
    }

    public void setCurrencyAbrev(String currencyAbrev) {
        this.currencyAbrev = currencyAbrev;
    }
}