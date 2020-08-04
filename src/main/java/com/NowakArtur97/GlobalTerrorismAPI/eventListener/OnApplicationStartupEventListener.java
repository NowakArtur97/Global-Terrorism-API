package com.NowakArtur97.GlobalTerrorismAPI.eventListener;

import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.UserDTO;
import com.NowakArtur97.GlobalTerrorismAPI.enums.XlsxColumnType;
import com.NowakArtur97.GlobalTerrorismAPI.node.*;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.*;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
class OnApplicationStartupEventListener {

    private final static String PATH_TO_FILE = "data/globalterrorismdb_0919dist-mini.xlsx";

    private final Map<String, GroupNode> groupsWithEvents = new HashMap<>();

    private final List<CountryNode> allCountries = new ArrayList<>();

    private final List<CityNode> allCities = new ArrayList<>();

    private final TargetService targetService;

    private final GenericService<EventNode, EventDTO> eventService;

    private final GenericService<GroupNode, GroupDTO> groupService;

    private final CountryService countryService;

    private final CityService cityService;

    private final UserService userService;

    @EventListener
    public void onApplicationStartup(ContextRefreshedEvent event) {

        if (targetService.isDatabaseEmpty()) {

            Sheet sheet = loadSheetFromFile();

            insertDataToDatabase(sheet);
        }
    }

    private Sheet loadSheetFromFile() {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(PATH_TO_FILE);

        Workbook workbook = StreamingReader.builder().rowCacheSize(10).bufferSize(4096).open(inputStream);

        return workbook.getSheetAt(0);
    }

    private void insertDataToDatabase(Sheet sheet) {

        saveUser();

        for (Row row : sheet) {

            CityNode city = saveCity(row);

            CountryNode country = saveCountry(row);

            TargetNode target = saveTarget(row, country);

            EventNode event = saveEvent(row, target, city);

            String groupName = getCellValueFromRowOnIndex(row, XlsxColumnType.GROUP_NAME.getIndex());

            manageGroup(groupName, event);
        }

        saveAllGroups();
    }

    private void saveUser() {

        userService.register(new UserDTO("testuser", "Password123!", "Password123!", "testuser123@email.com"));
    }

    private void manageGroup(String groupName, EventNode event) {

        if (groupsWithEvents.containsKey(groupName)) {

            groupsWithEvents.get(groupName).addEvent(event);

        } else {

            GroupNode group = new GroupNode(groupName);

            group.addEvent(event);

            if (isUnknown(groupName)) {

                groupService.save(group);

            } else {

                groupsWithEvents.put(groupName, group);
            }
        }
    }

    private void saveAllGroups() {

        for (GroupNode group : groupsWithEvents.values()) {

            groupService.save(group);
        }
    }

    private EventNode saveEvent(Row row, TargetNode target, CityNode city) {

        String cellValue;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.YEAR_OF_EVENT.getIndex());
        int yearOfEvent = isNumeric(cellValue) ? parseInt(cellValue) : 1900;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.MONTH_OF_EVENT.getIndex());
        int monthOfEvent = isNumeric(cellValue) ? parseInt(cellValue) : 1;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.DAY_OF_EVENT.getIndex());
        int dayOfEvent = isNumeric(cellValue) ? parseInt(cellValue) : 1;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.EVENT_SUMMARY.getIndex());
        String eventSummary = !cellValue.isEmpty() ? cellValue : "";

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.EVENT_MOTIVE.getIndex());
        String motive = !cellValue.isEmpty() ? cellValue : "";

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.WAS_EVENT_PART_OF_MULTIPLE_INCIDENTS.getIndex());
        boolean isPartOfMultipleIncidents = parseBoolean(cellValue);

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.WAS_EVENT_SUCCESS.getIndex());
        boolean isSuccessful = parseBoolean(cellValue);

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.WAS_EVENT_SUICIDE.getIndex());
        boolean isSuicidal = parseBoolean(cellValue);

        Date date = getEventDate(yearOfEvent, monthOfEvent, dayOfEvent);

        return eventService.save(EventNode.builder().date(date).summary(eventSummary)
                .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                .isSuicidal(isSuicidal).motive(motive).target(target).city(city).build());
    }

    private TargetNode saveTarget(Row row, CountryNode country) {

        String targetName = getCellValueFromRowOnIndex(row, XlsxColumnType.TARGET_NAME.getIndex());

        return targetService.save(new TargetNode(targetName, country));
    }

    private CountryNode saveCountry(Row row) {

        String name = getCellValueFromRowOnIndex(row, XlsxColumnType.COUNTRY_NAME.getIndex());

        CountryNode country = new CountryNode(name);

        if (allCountries.contains(country)) {

            return allCountries.get(allCountries.indexOf(country));

        } else {

            allCountries.add(country);

            countryService.save(country);

            return country;
        }
    }

    private CityNode saveCity(Row row) {

        String cellValue;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.CITY_NAME.getIndex());
        String name = cellValue;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.CITY_LATITUDE.getIndex());
        double latitude = isNumeric(cellValue) ? parseDouble(cellValue) : 0;

        cellValue = getCellValueFromRowOnIndex(row, XlsxColumnType.CITY_LONGITUDE.getIndex());
        double longitude = isNumeric(cellValue) ? parseDouble(cellValue) : 0;

        CityNode city = new CityNode(name, latitude, longitude);

        if (allCities.contains(city) && !isUnknown(name)) {

            return allCities.get(allCities.indexOf(city));

        } else {

            allCities.add(city);

            cityService.save(city);

            return city;
        }
    }

    private Date getEventDate(int yearOfEvent, int monthOfEvent, int dayOfEvent) {

        monthOfEvent = isMonthCorrect(monthOfEvent) ? monthOfEvent - 1 : 0;
        dayOfEvent = isDayCorrect(dayOfEvent) ? dayOfEvent : 1;

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, yearOfEvent);
        cal.set(Calendar.MONTH, monthOfEvent);
        cal.set(Calendar.DAY_OF_MONTH, dayOfEvent);

        return cal.getTime();
    }

    private String getCellValueFromRowOnIndex(Row row, int index) {

        Cell cell = row.getCell(index, MissingCellPolicy.CREATE_NULL_AS_BLANK);

        String value = "";

        switch (cell.getCellType()) {

            case NUMERIC:
                double doubleValue = cell.getNumericCellValue();
                value = Double.toString(doubleValue);
                break;

            case STRING:
                value = cell.getStringCellValue();
                break;

            case FORMULA:
                value = cell.getCellFormula();
                break;

            case BOOLEAN:
                boolean booleanValue = cell.getBooleanCellValue();
                value = "" + booleanValue;
                break;

            case ERROR:
                byte byteValue = cell.getErrorCellValue();
                value = "" + byteValue;
                break;

            case BLANK:
            case _NONE:
            default:
                break;
        }

        return value;
    }

    private boolean isUnknown(String name) {

        return name.equalsIgnoreCase("unknown");
    }

    private boolean isMonthCorrect(int monthOfEvent) {

        return monthOfEvent > 0 && monthOfEvent <= 12;
    }

    private boolean isDayCorrect(int dayOfEvent) {

        return dayOfEvent > 0 && dayOfEvent <= 31;
    }

    private boolean isNumeric(String number) {

        return NumberUtils.isParsable(number);
    }

    private int parseInt(String stringToParse) {

        return (int) Double.parseDouble(stringToParse);
    }

    private double parseDouble(String stringToParse) {

        return Double.parseDouble(stringToParse);
    }

    private boolean parseBoolean(String stringToParse) {

        return "1".equals(stringToParse) || "1.0".equals(stringToParse);
    }
}