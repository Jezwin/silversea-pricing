package com.silversea.aem.components.beans;

/**
 * TODO replace by {@link java.time.Duration}
 */
@Deprecated
public class Duration {

    double hours;

    double minutes;

    public Duration() {

    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getMinutes() {
        return minutes;
    }

    public void setMinutes(double minutes) {
        this.minutes = minutes;
    }
}
