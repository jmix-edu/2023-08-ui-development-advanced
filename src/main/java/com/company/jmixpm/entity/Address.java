package com.company.jmixpm.entity;

import io.jmix.core.metamodel.annotation.JmixEntity;

import javax.persistence.*;

@JmixEntity
@Embeddable
public class Address {
    @JoinColumn(name = "CITY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private City city;

    @Column(name = "STREET")
    private String street;

    @Column(name = "ZIPCODE")
    private String zipcode;

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}