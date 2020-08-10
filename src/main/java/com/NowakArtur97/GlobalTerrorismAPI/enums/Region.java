package com.NowakArtur97.GlobalTerrorismAPI.enums;

public enum Region {

    NORTH_AMERICA(), CENTRAL_AMERICA_AND_CARIBBEAN(), SOUTH_AMERICA(), EAST_ASIA(), SOUTHEAST_ASIA(), SOUTH_ASIA(),
    CENTRAL_ASIA(), WESTERN_EUROPE(), EASTERN_EUROPE(), MIDDLE_EAST_AND_NORTH_AFRICA(), SUB_SAHARAN_AFRICA(),
    AUSTRALASIA_AND_OCEANIA();

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
