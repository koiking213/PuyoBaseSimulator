package com.example.puyo_base_simulator.ui.home;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;

import java.io.Serializable;
import java.util.Collections;

import static org.junit.Assert.*;

public class FieldTest {
    private Field mField;

    @Before
    public void setUp() {
        /*
         g
         rggbbr
         bbbgrr
         rrrbbb
        */
        String str = "rrrbbbbbbgrrrggbbrg";
        mField = new Field(str);
    }

    private String fillFieldString(String str) {
        int remain = 6*13 - str.length();
        return str + String.join("", Collections.nCopies(remain, " "));
    }

    @Test
    public void addPuyo() {
        // add one puyo and check field content and heights
        Field f = (Field) SerializationUtils.clone(mField);
        assertThat(f.addPuyo(1, PuyoColor.RED)).isTrue();
        String expected = fillFieldString("rrrbbbbbbgrrrggbbrg     r");
        assertThat(f.toString()).isEqualTo(expected);
        assertThat(f.heights[1]).isEqualTo(mField.heights[1]+1);
        for (int i=2; i<=6; i++) {
            assertThat(f.heights[i]).isEqualTo(mField.heights[i]);
        }

        // fail add puyo
        for (int i=0; i<20; i++) {
            if (f.heights[1] == 13) {
                assertThat(f.addPuyo(1, PuyoColor.RED)).isFalse();
            } else {
                assertThat(f.addPuyo(1, PuyoColor.RED)).isTrue();
            }
        }
    }

    @Test
    public void allClear() {
        assertThat(mField.allClear()).isFalse();
        assertThat((new Field()).allClear()).isTrue();
    }

    @Test
    public void isDisappear() {
        Field f = (Field) SerializationUtils.clone(mField);
        f.evalNextField();
        assertThat(f.disappearPuyo.isEmpty()).isTrue();
        f.addPuyo(6, PuyoColor.RED);
        f.evalNextField();  // 4個消し5連鎖
        assertThat(f.disappearPuyo.isEmpty()).isFalse();
        assertThat(f.disappearPuyo.size()).isEqualTo(4);
        f = f.nextField;
        assertThat(f.disappearPuyo.size()).isEqualTo(4);
        f = f.nextField;
        assertThat(f.disappearPuyo.size()).isEqualTo(4);
        f = f.nextField;
        assertThat(f.disappearPuyo.size()).isEqualTo(4);
        f = f.nextField;
        assertThat(f.disappearPuyo.size()).isEqualTo(4);
    }

    @Test
    public void evalNextField() {
        Field f = (Field) SerializationUtils.clone(mField);
        f.evalNextField();
        assertThat(f.disappearPuyo.isEmpty()).isTrue();
        f.addPuyo(6, PuyoColor.RED);
        f.evalNextField();  // 4個消し5連鎖
        assertThat(f.accumulatedPoint).isEqualTo(40);
        f = f.nextField;
        assertThat(f.accumulatedPoint).isEqualTo(360);
        f = f.nextField;
        assertThat(f.accumulatedPoint).isEqualTo(1000);
        f = f.nextField;
        assertThat(f.accumulatedPoint).isEqualTo(2280);
        f = f.nextField;
        assertThat(f.accumulatedPoint).isEqualTo(4840);
        assertThat(f.allClear()).isTrue();
    }

    @Test
    public void testToString() {
        String expected = fillFieldString("rrrbbbbbbgrrrggbbrg");
        assertThat(mField.toString()).isEqualTo(expected);
    }
}