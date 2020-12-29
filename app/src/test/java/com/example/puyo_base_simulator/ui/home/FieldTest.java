package com.example.puyo_base_simulator.ui.home;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FieldTest {
    private Field mField;

    @Before
    public void setUp() throws Exception {
        /*
         g
         rggbbr
         bbbgrr
         rrrbbb
        */
        String str = "rrrbbbbbbgrrrggbbrg";
        mField = new Field(str);
    }

    @Test
    public void testClone() {
    }

    @Test
    public void addPuyo() {
    }

    @Test
    public void allClear() {
    }

    @Test
    public void isDisappear() {
    }

    @Test
    public void evalNextField() {
    }

    @Test
    public void getNeighborPuyo() {
    }

    @Test
    public void getConnection() {
    }

    @Test
    public void testToString() {
    }
}