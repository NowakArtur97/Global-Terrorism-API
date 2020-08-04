package com.NowakArtur97.GlobalTerrorismAPI.enums;

import lombok.Getter;

public enum XlsxColumnType {

    YEAR_OF_EVENT(1), MONTH_OF_EVENT(2), DAY_OF_EVENT(3), COUNTRY_NAME(8),
    CITY_NAME(12), CITY_LATITUDE(13),  CITY_LONGITUDE(14),
    EVENT_SUMMARY(18), WAS_EVENT_PART_OF_MULTIPLE_INCIDENTS(25), WAS_EVENT_SUCCESS(26),
    WAS_EVENT_SUICIDE(27), TARGET_NAME(39), GROUP_NAME(58), EVENT_MOTIVE(64);

    @Getter
    private int index;

    XlsxColumnType(int index) {

        this.index = index;
    }
}
