package com.example.eats.Geohashing;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeohasherTest {

    Geohasher geohasher = null;

    @Test
    public void geoHash() {
        geohasher = new Geohasher(37.481397,-122.170806);
        assertEquals("9q9j5zmbvyu6", geohasher.geoHash(12));
    }

    @Test
    public void geoHash2() {
        geohasher = new Geohasher(37.495668, -122.275150);
        assertEquals("9q9j39cun7pg", geohasher.geoHash(12));
    }

    @Test
    public void geoHash3() {
        geohasher = new Geohasher(37.483913, -122.285654);
        assertEquals("9q9j1rdr9ezd", geohasher.geoHash(12));
    }

    @Test
    public void geoHash4() {
        geohasher = new Geohasher(37.483325, -122.317168);
        assertEquals("9q9j0xek5qhz", geohasher.geoHash(12));
    }

    @Test
    public void geoHash5() {
        geohasher = new Geohasher(40.712982, -74.007205);
        assertEquals("dr5regtd71um", geohasher.geoHash(12));
    }

    @Test
    public void geoHash6() {
        geohasher = new Geohasher(-13.130077, 28.420100);
        assertEquals("ktsjq9wzu24x", geohasher.geoHash(12));
    }

    @Test
    public void geoHash7() {
        geohasher = new Geohasher(-12.809478, 28.213542);
        assertEquals("ktsp4h07rhjx", geohasher.geoHash(12));
    }

    @Test
    public void geoHash8() {
        geohasher = new Geohasher(36.166747, -115.148708);
        assertEquals("9qqjexzq8dk1", geohasher.geoHash(12));
    }

    @Test
    public void geoHash9() {
        geohasher = new Geohasher(37.483913, -122.285654);
        assertEquals("9q9j1rdr9ezd", geohasher.geoHash(12));
    }

    @Test
    public void geoHash10() {
        geohasher = new Geohasher(48.856788, 2.351077);
        assertEquals("u09tvw052j6h", geohasher.geoHash(12));
    }
}