package com.nowakArtur97.globalTerrorismAPI.eventListener;

import com.monitorjbl.xlsx.StreamingReader;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;
import com.nowakArtur97.globalTerrorismAPI.common.util.XlsxUtil;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityService;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryService;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventNode;
import com.nowakArtur97.globalTerrorismAPI.feature.group.GroupDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.group.GroupNode;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.province.ProvinceNode;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionNode;
import com.nowakArtur97.globalTerrorismAPI.feature.region.RegionService;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetService;
import com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.UserDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.user.registerUser.UserService;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimNode;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
class OnApplicationStartupEventListener {

    private static final String DEFAULT_EVENT_SUMMARY = "The specific summary of the attack is unknown.";
    private static final String DEFAULT_EVENT_MOTIVE = "The specific motive of the attack is unknown.";
    public static final int DEFAULT_YEAR_OF_EVENT = 1970;
    public static final int DEFAULT_MONTH_OF_EVENT = 1;
    public static final int DEFAULT_DAY_OF_EVENT = 1;
    private static final String DEFAULT_TARGET = "The specific target of the attack is unknown.";

    @Value("${app.dataFilePath:data/globalterrorismdb_0919dist-mini.xlsx}")
    private String PATH_TO_FILE;

    private final static int NUMBER_OF_ROWS_TO_SKIP = 850;

    private final Map<String, GroupNode> groupsWithEvents = new HashMap<>();

    private final List<CountryNode> allCountries = new ArrayList<>();

    private final List<ProvinceNode> allProvinces = new ArrayList<>();

    private final List<CityNode> allCities = new ArrayList<>();

    private final List<RegionNode> allRegions = new ArrayList<>();

    private final TargetService targetService;

    private final GenericService<EventNode, EventDTO> eventService;

    private final GenericService<GroupNode, GroupDTO> groupService;

    private final GenericService<ProvinceNode, ProvinceDTO> provinceService;

    private final CountryService countryService;

    private final RegionService regionService;

    private final CityService cityService;

    private final VictimService victimService;

    private final UserService userService;

    private InputStream inputStream;
    private Workbook workbook;

    @EventListener
    void onApplicationStartup(ContextRefreshedEvent event) {

        if (targetService.isDatabaseEmpty()) {

            try {
                Sheet sheet = loadSheetFromFile();

                log.info("##################### Inserting data to database #####################");

                insertDataToDatabase(sheet);

                workbook.close();
                inputStream.close();

                log.info("##################### All data inserted #####################");

            } catch (NullPointerException | FileNotFoundException e) {

                log.info("##################### File: " + PATH_TO_FILE + " not found #####################");

                e.printStackTrace();

            } catch (IOException e) {

                log.info("##################### Couldn't load data #####################");

                e.printStackTrace();
            }
        }
    }

    private Sheet loadSheetFromFile() {

        inputStream = this.getClass().getClassLoader().getResourceAsStream(PATH_TO_FILE);

        workbook = StreamingReader.builder().rowCacheSize(10).bufferSize(4096).open(inputStream);

        return workbook.getSheetAt(0);
    }

    private void insertDataToDatabase(Sheet sheet) {

        saveDefaultUser();

        int numberOfRows = sheet.getLastRowNum();
        int rowIndexToSave = 1;
        int rowIndex = 0;

        for (Row row : sheet) {

            if (rowIndexToSave == rowIndex) {

                RegionNode region = saveRegion(row);

                CountryNode country = saveCountry(row, region);

                ProvinceNode province = saveProvince(row, country);

                CityNode city = saveCity(row, province);

                TargetNode target = saveTarget(row, country);

                VictimNode victim = saveVictim(row);

                EventNode event = saveEvent(row, target, city, victim);

                String groupName = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.GROUP_NAME.getIndex());

                manageGroup(groupName, event);

                rowIndexToSave += NUMBER_OF_ROWS_TO_SKIP;
            }

            if (numberOfRows <= rowIndex) {
                break;
            }

            rowIndex++;
        }

        saveAllGroups();
    }

    private void saveDefaultUser() {

        userService.register(new UserDTO("testuser", "Password123!", "Password123!",
                "testuser123@email.com"));
    }

    private void manageGroup(String groupName, EventNode event) {

        if (groupsWithEvents.containsKey(groupName)) {

            groupsWithEvents.get(groupName).addEvent(event);

        } else {

            GroupNode group = new GroupNode(groupName);

            group.addEvent(event);

            if (XlsxUtil.isUnknown(groupName)) {

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

    private EventNode saveEvent(Row row, TargetNode target, CityNode city, VictimNode victim) {

        String cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.YEAR_OF_EVENT.getIndex());
        int yearOfEvent = XlsxUtil.isNumeric(cellValue) ? XlsxUtil.parseInt(cellValue) : DEFAULT_YEAR_OF_EVENT;

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.MONTH_OF_EVENT.getIndex());
        int monthOfEvent = XlsxUtil.isNumeric(cellValue) ? XlsxUtil.parseInt(cellValue) : DEFAULT_MONTH_OF_EVENT;

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.DAY_OF_EVENT.getIndex());
        int dayOfEvent = XlsxUtil.isNumeric(cellValue) ? XlsxUtil.parseInt(cellValue) : DEFAULT_DAY_OF_EVENT;

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.EVENT_SUMMARY.getIndex());
        String eventSummary = cellValue.isEmpty() || XlsxUtil.isUnknown(cellValue) ? DEFAULT_EVENT_SUMMARY : cellValue;

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.EVENT_MOTIVE.getIndex());
        String motive = cellValue.isEmpty() || XlsxUtil.isUnknown(cellValue) ? DEFAULT_EVENT_MOTIVE : cellValue;

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.WAS_EVENT_PART_OF_MULTIPLE_INCIDENTS.getIndex());
        boolean isPartOfMultipleIncidents = XlsxUtil.parseBoolean(cellValue);

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.WAS_EVENT_SUCCESS.getIndex());
        boolean isSuccessful = XlsxUtil.parseBoolean(cellValue);

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.WAS_EVENT_SUICIDE.getIndex());
        boolean isSuicidal = XlsxUtil.parseBoolean(cellValue);

        Date date = XlsxUtil.getDate(yearOfEvent, monthOfEvent, dayOfEvent);

        return eventService.save(EventNode.builder().date(date).summary(eventSummary)
                .isPartOfMultipleIncidents(isPartOfMultipleIncidents).isSuccessful(isSuccessful)
                .isSuicidal(isSuicidal).motive(motive).target(target).city(city).victim(victim).
                        build());
    }

    private TargetNode saveTarget(Row row, CountryNode country) {

        String cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.TARGET_NAME.getIndex());
        String targetName = cellValue.isEmpty() || XlsxUtil.isUnknown(cellValue) ? DEFAULT_TARGET : cellValue;

        return targetService.save(new TargetNode(targetName, country));
    }


    private VictimNode saveVictim(Row row) {

        String cellValue;

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.TOTAL_NUMBER_OF_FATALITIES.getIndex());
        long totalNumberOfFatalities = 0;
        totalNumberOfFatalities = XlsxUtil.getPositiveValue(cellValue, totalNumberOfFatalities);

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.NUMBER_OF_PERPETRATOR_FATALITIES.getIndex());
        long numberOfPerpetratorFatalities = 0;
        numberOfPerpetratorFatalities = XlsxUtil.getPositiveValue(cellValue, numberOfPerpetratorFatalities);

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.TOTAL_NUMBER_OF_INJURED.getIndex());
        long totalNumberOfInjured = 0;
        totalNumberOfInjured = XlsxUtil.getPositiveValue(cellValue, totalNumberOfInjured);

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.NUMBER_OF_PERPETRATOR_INJURED.getIndex());
        long numberOfPerpetratorInjured = 0;
        numberOfPerpetratorInjured = XlsxUtil.getPositiveValue(cellValue, numberOfPerpetratorInjured);

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.VALUE_OF_PROPERTY_DAMAGE.getIndex());
        long valueOfPropertyDamage = 0;
        valueOfPropertyDamage = XlsxUtil.getPositiveValue(cellValue, valueOfPropertyDamage);

        VictimNode victim = VictimNode.builder()
                .totalNumberOfFatalities(totalNumberOfFatalities)
                .numberOfPerpetratorsFatalities(numberOfPerpetratorFatalities)
                .totalNumberOfInjured(totalNumberOfInjured)
                .numberOfPerpetratorsInjured(numberOfPerpetratorInjured)
                .valueOfPropertyDamage(valueOfPropertyDamage)
                .build();

        return victimService.save(victim);
    }

    private CountryNode saveCountry(Row row, RegionNode regionNode) {

        String name = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.COUNTRY_NAME.getIndex());

        CountryNode country = new CountryNode(name, regionNode);

        if (allCountries.contains(country)) {

            return allCountries.get(allCountries.indexOf(country));

        } else {

            countryService.save(country);

            allCountries.add(country);

            return country;
        }
    }

    private RegionNode saveRegion(Row row) {

        String name = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.REGION_NAME.getIndex());

        RegionNode region = new RegionNode(name);

        if (allRegions.contains(region)) {

            return allRegions.get(allRegions.indexOf(region));

        } else {

            regionService.save(region);

            allRegions.add(region);

            return region;
        }
    }

    private ProvinceNode saveProvince(Row row, CountryNode country) {

        String name = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.PROVINCE_NAME.getIndex());

        ProvinceNode province = new ProvinceNode(name, country);

        if (allProvinces.contains(province)) {

            return allProvinces.get(allProvinces.indexOf(province));

        } else {

            provinceService.save(province);

            allProvinces.add(province);

            return province;
        }
    }

    private CityNode saveCity(Row row, ProvinceNode province) {

        String cellValue;

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.CITY_NAME.getIndex());
        String name = cellValue;

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.CITY_LATITUDE.getIndex());
        double latitude = XlsxUtil.isNumeric(cellValue) ? Double.parseDouble(cellValue) : 0;

        cellValue = XlsxUtil.getCellValueFromRowOnIndex(row, XlsxColumnType.CITY_LONGITUDE.getIndex());
        double longitude = XlsxUtil.isNumeric(cellValue) ? Double.parseDouble(cellValue) : 0;

        CityNode city = new CityNode(name, latitude, longitude, province);

        if (allCities.contains(city)) {

            return allCities.get(allCities.indexOf(city));

        } else {

            cityService.save(city);

            allCities.add(city);

            return city;
        }
    }
}