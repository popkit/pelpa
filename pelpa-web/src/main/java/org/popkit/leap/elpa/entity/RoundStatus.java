package org.popkit.leap.elpa.entity;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:21:21
 */
public enum RoundStatus {
    READY("ready"),
    RUNNING("building"),
    REST("finished"),
    FINISHED("finished"),
    ;

    private String value;

    private RoundStatus(String value) {
        this.value = value;
    }
}
