package com.NowakArtur97.GlobalTerrorismAPI.enums;

public enum Region {

    NORTH_AMERICA("North America"), CENTRAL_AMERICA_AND_CARIBBEAN("Central America & Caribbean"),
    SOUTH_AMERICA("South America"), EAST_ASIA("East Asia"), SOUTHEAST_ASIA("Southeast Asia"),
    SOUTH_ASIA("South Asia"), CENTRAL_ASIA("Central Asia"), WESTERN_EUROPE("Western Europe"),
    EASTERN_EUROPE("Eastern Europe"), MIDDLE_EAST_AND_NORTH_AFRICA("Middle East & North Africa"),
    SUB_SAHARAN_AFRICA("Sub-Saharan Africa"), AUSTRALASIA_AND_OCEANIA("Australasia & Oceania");

    Region(String name) {
    }

    public static Region getRegionByName(String name) {

        switch (name) {
            case "North America":
                return NORTH_AMERICA;
            case "Central America & Caribbean":
                return CENTRAL_AMERICA_AND_CARIBBEAN;
            case "South America":
                return SOUTH_AMERICA;
            case "East Asia":
                return EAST_ASIA;
            case "Southeast Asia":
                return SOUTHEAST_ASIA;
            case "South Asia":
                return SOUTH_ASIA;
            case "Central Asia":
                return CENTRAL_ASIA;
            case "Western Europe":
                return WESTERN_EUROPE;
            case "Eastern Europe":
                return EASTERN_EUROPE;
            case "Middle East & North Africa":
                return MIDDLE_EAST_AND_NORTH_AFRICA;
            case "Sub-Saharan Africa":
                return SUB_SAHARAN_AFRICA;
            case "Australasia & Oceania":
                return AUSTRALASIA_AND_OCEANIA;
        }

        throw new IllegalArgumentException(name);
    }
}
