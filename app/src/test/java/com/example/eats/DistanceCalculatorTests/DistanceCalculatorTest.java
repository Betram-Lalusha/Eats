package com.example.eats.DistanceCalculatorTests;

import static org.junit.Assert.assertEquals;

import com.example.eats.Geohashing.Geohasher;
import com.example.eats.Helpers.DistanceCalculator;

import org.junit.Test;

public class DistanceCalculatorTest {
    public DistanceCalculator mDistanceCalculator = new DistanceCalculator("K",37.481359, -122.170809 );

    @Test
    public void distance() {

       assertEquals(0.482803, mDistanceCalculator.distance(37.484361, -122.174190), 0.8);
    }

    @Test
    public void distance2() {
        assertEquals(1.77028, mDistanceCalculator.distance(37.488001, -122.189016), 0.8);
    }

    @Test
    public void distance3() {
        assertEquals(0, mDistanceCalculator.distance(37.481359, -122.170809), 0.8);
    }

    //4165
    @Test
    public void distance4() {
        assertEquals(12786.74, mDistanceCalculator.distance(0.0, 0.0), 0.8);
    }
}
