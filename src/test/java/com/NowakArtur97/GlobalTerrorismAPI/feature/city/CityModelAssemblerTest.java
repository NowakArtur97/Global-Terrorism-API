package com.NowakArtur97.GlobalTerrorismAPI.feature.city;

import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CityBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.ProvinceBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("CityModelAssembler_Tests")
class CityModelAssemblerTest {

    private final String PROVINCE_BASE_PATH = "http://localhost/api/v1/provinces";
    private final String CITY_BASE_PATH = "http://localhost/api/v1/cities";

    private CityModelAssembler modelAssembler;

    @Mock
    private ProvinceModelAssembler provinceModelAssembler;

    @Mock
    private ModelMapper modelMapper;

    private static ProvinceBuilder provinceBuilder;
    private static CityBuilder cityBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        provinceBuilder = new ProvinceBuilder();
        cityBuilder = new CityBuilder();
    }

    @BeforeEach
    private void setUp() {

        modelAssembler = new CityModelAssembler(provinceModelAssembler, modelMapper);
    }

    @Test
    void when_map_city_node_to_model_should_return_city_model() {

        Long provinceId = 1L;
        Long cityId = 2L;
        ProvinceNode provinceNode = (ProvinceNode) provinceBuilder.withId(provinceId).build(ObjectType.NODE);
        CityNode cityNode = (CityNode) cityBuilder.withId(cityId).withProvince(provinceNode).build(ObjectType.NODE);
        ProvinceModel provinceModelExpected = (ProvinceModel) provinceBuilder.withId(provinceId).build(ObjectType.MODEL);
        String pathToProvinceLink = PROVINCE_BASE_PATH + "/" + provinceId.intValue();
        provinceModelExpected.add(new Link(pathToProvinceLink));
        CityModel cityModelExpected = (CityModel) cityBuilder.withId(cityId).withProvince(provinceModelExpected)
                .build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + cityId.intValue();

        when(modelMapper.map(cityNode, CityModel.class)).thenReturn(cityModelExpected);
        when(provinceModelAssembler.toModel(provinceNode)).thenReturn(provinceModelExpected);

        CityModel cityModelActual = modelAssembler.toModel(cityNode);

        assertAll(
                () -> assertEquals(pathToCityLink, cityModelActual.getLink("self").get().getHref(),
                        () -> "should return city model with self link: " + pathToCityLink + ", but was: "
                                + cityModelActual.getLink("self").get().getHref()),
                () -> assertEquals(pathToProvinceLink, cityModelActual.getProvince().getLink("self").get().getHref(),
                        () -> "should return city province model with self link: " + pathToProvinceLink + ", but was: "
                                + cityModelActual.getProvince().getLink("self").get().getHref()),

                () -> assertNotNull(cityModelActual, () -> "should return not null city model, but was: null"),
                () -> assertEquals(cityModelExpected.getId(), cityModelActual.getId(),
                        () -> "should return city model with city model id: " + cityModelExpected.getId() + ", but was: "
                                + cityModelActual.getId()),
                () -> assertEquals(cityModelExpected.getName(), cityModelActual.getName(),
                        () -> "should return city model with name: " + cityModelExpected.getName() + ", but was: "
                                + cityModelActual.getName()),
                () -> assertEquals(cityModelExpected.getLatitude(), cityModelActual.getLatitude(),
                        () -> "should return city model with latitude: " +
                                cityModelExpected.getLatitude() + ", but was: " + cityModelActual.getLatitude()),
                () -> assertEquals(cityModelExpected.getLongitude(), cityModelActual.getLongitude(),
                        () -> "should return city model with longitude: " +
                                cityModelExpected.getLongitude() + ", but was: " + cityModelActual.getLongitude()),

                () -> assertNotNull(cityModelExpected.getProvince(),
                        () -> "should return city model with not null province, but was: null"),
                () -> assertEquals(provinceModelExpected, cityModelExpected.getProvince(),
                        () -> "should return city model with province: " + provinceModelExpected + ", but was: "
                                + cityModelExpected.getProvince()),
                () -> assertEquals(provinceModelExpected.getId(), cityModelExpected.getProvince().getId(),
                        () -> "should return city model with province id: " + provinceModelExpected.getId()
                                + ", but was: " + cityModelExpected.getProvince().getId()),
                () -> assertEquals(provinceModelExpected.getName(), cityModelExpected.getProvince().getName(),
                        () -> "should return city model with province name: " + provinceModelExpected.getName() + ", but was: "
                                + cityModelExpected.getProvince().getName()),

                () -> assertFalse(cityModelActual.getLinks().isEmpty(),
                        () -> "should return city model with links, but wasn't"),
                () -> assertFalse(cityModelActual.getProvince().getLinks().isEmpty(),
                        () -> "should return city model with province model with links, but wasn't"),
                () -> verify(modelMapper, times(1)).map(cityNode, CityModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(provinceModelAssembler, times(1)).toModel(provinceNode),
                () -> verifyNoMoreInteractions(provinceModelAssembler));
    }

    @Test
    void when_map_city_node_without_province_to_model_should_return_city_model_without_province() {

        Long cityId = 2L;
        CityNode cityNode = (CityNode) cityBuilder.withId(cityId).build(ObjectType.NODE);
        CityModel cityModelExpected = (CityModel) cityBuilder.withId(cityId).build(ObjectType.MODEL);
        String pathToCityLink = CITY_BASE_PATH + "/" + cityId.intValue();
        cityModelExpected.add(new Link("self", pathToCityLink));

        when(modelMapper.map(cityNode, CityModel.class)).thenReturn(cityModelExpected);

        CityModel cityModelActual = modelAssembler.toModel(cityNode);

        assertAll(
                () -> assertEquals(pathToCityLink, cityModelActual.getLink("self").get().getHref(),
                        () -> "should return city model with self link: " + pathToCityLink + ", but was: "
                                + cityModelActual.getLink("self").get().getHref()),

                () -> assertNotNull(cityModelActual, () -> "should return not null city model, but was: null"),
                () -> assertEquals(cityModelExpected.getId(), cityModelActual.getId(),
                        () -> "should return city model with city model id: " + cityModelExpected.getId() + ", but was: "
                                + cityModelActual.getId()),
                () -> assertEquals(cityModelExpected.getName(), cityModelActual.getName(),
                        () -> "should return city model with name: " + cityModelExpected.getName() + ", but was: "
                                + cityModelActual.getName()),
                () -> assertEquals(cityModelExpected.getLatitude(), cityModelActual.getLatitude(),
                        () -> "should return city model with province city latitude: " +
                                cityModelExpected.getLatitude() + ", but was: " + cityModelActual.getLatitude()),
                () -> assertEquals(cityModelExpected.getLongitude(), cityModelActual.getLongitude(),
                        () -> "should return city model with province city longitude: " +
                                cityModelExpected.getLongitude() + ", but was: " + cityModelActual.getLongitude()),

                () -> assertNull(cityModelExpected.getProvince(),
                        () -> "should return city model with null province, but was: " + cityModelExpected.getProvince()),

                () -> assertFalse(cityModelActual.getLinks().isEmpty(),
                        () -> "should return city model with links, but wasn't"),
                () -> verify(modelMapper, times(1)).map(cityNode, CityModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verifyNoInteractions(provinceModelAssembler));
    }
}
