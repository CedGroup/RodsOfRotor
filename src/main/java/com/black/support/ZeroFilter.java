package com.black.support;

/**
 * Created by Nick on 14.06.2016.
 */
public class ZeroFilter {
    private Float valueToSave = 0F;

    public Float filtered(Float value, Float value2){
        if (value < 0.3) {
            value = valueToSave;
        } else {
            valueToSave = value;
        }

        if (value2 == 0){
            value = 0F;
        }

        return value;
    }

    public Float filtered(Float value){
        if (value < 0.3) {
            value = valueToSave;
        } else {
            valueToSave = value;
        }

        return value;
    }
}
