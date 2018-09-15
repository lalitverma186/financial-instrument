package com.luxoft.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "INSTRUMENT_PRICE_MODIFIER")
public class InstrumentPriceModifier {

    @Id
    @GeneratedValue
    @PrimaryKeyJoinColumn
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MULTIPLIER")
    private int multiplier;

    public InstrumentPriceModifier() {
    }

    public InstrumentPriceModifier(String name, int multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }

    /**
     * Retrieves the value for {@link #id}.
     *
     * @return the current value
     */
    public int getId() {
        return id;
    }

    /**
     * Provides a value for {@link #id}.
     *
     * @param id the new value to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the value for {@link #name}.
     *
     * @return the current value
     */
    public String getName() {
        return name;
    }

    /**
     * Provides a value for {@link #name}.
     *
     * @param name the new value to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the value for {@link #multiplier}.
     *
     * @return the current value
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * Provides a value for {@link #multiplier}.
     *
     * @param multiplier the new value to set
     */
    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * {@inheritDoc} Required implementation.
     */
    @Override
    public String toString() {
        return "InstrumentPriceModifier [id=" + id + ", name=" + name + ", multiplier=" + multiplier + "]";
    }

}
