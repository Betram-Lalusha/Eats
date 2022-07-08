package com.example.eats.Geohashing;


/**
 * Based on Gustavo Niemeyer's geocoding system
 */
public class Geohasher {

    public double mLatitude;
    public double mLongitude;
    public final String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    public Geohasher(Double latitude, Double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    /**
     * Method converts latitudes and longitude coordinates into a geohash using Gustavo Niemeyer's
     * geocoding system and unique base32. The method takes the given coordinates and repeatedly halves them
     * until the required accuracy is met.
     * @param precision: number indicates how long, precise, the resulting string should nbe
     * @return: the geohash of the given coordinates
     */
    public String geoHash(int precision) {

        int index = 0;
        String geohash = "";
        //Double midLat = 0.0;
        ///Double midLong = 0.0;
        int numberOfBits = 0;
        Double maxLat = 90.0;
        Double minLat = -90.0;
        Double maxLong = 180.0;
        Double minLong = -180.0;
        Boolean evenBit = true;
        Double latitude =  this.mLatitude;
        Double longitude = this.mLongitude;

        while (geohash.length() < precision) {
            System.out.println(evenBit);
            if(evenBit) {
                //test every line
                //half longitude
                Double midLong = (minLong + maxLong) / 2;
                if(longitude >= midLong) {
                    index = index * 2  + 1;
                    minLong = midLong;
                } else {
                    index = index * 2;
                    maxLong = midLong;
                }
            } else {
                //half latitude
                Double midLat = (minLat + maxLat) / 2;
                if(latitude >= midLat) {
                    index = index * 2 + 1;
                    minLat = midLat;
                } else {
                    index = index * 2;
                    maxLat = midLat;
                }
            }

            evenBit = !evenBit;

            //vysor
            numberOfBits++;
            if(numberOfBits == 5) {
                geohash += base32.charAt(index);
                index = 0;
                numberOfBits = 0;
            }
        }


        return geohash;
    }

}
