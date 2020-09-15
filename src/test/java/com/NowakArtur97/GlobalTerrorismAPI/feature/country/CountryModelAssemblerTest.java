package com.NowakArtur97.GlobalTerrorismAPI.feature.country;

import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.RegionBuilder;
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
@Tag("CountryModelAssembler_Tests")
class CountryModelAssemblerTest {

    private final String REGION_BASE_PATH = "http://localhost/api/v1/regions";
    private final String COUNTRY_BASE_PATH = "http://localhost/api/v1/countries";

    private CountryModelAssembler modelAssembler;

    @Mock
    private RegionModelAssembler regionModelAssembler;

    @Mock
    private ModelMapper modelMapper;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
    }

    @BeforeEach
    private void setUp() {

        modelAssembler = new CountryModelAssembler(regionModelAssembler, modelMapper);
    }

    @Test
    void when_map_country_node_to_model_should_return_country_model() {

        Long regionId = 1L;
        Long countryId = 2L;
        RegionNode regionNode = (RegionNode) regionBuilder.withId(regionId).build(ObjectType.NODE);
        CountryNode countryNode = (CountryNode) countryBuilder.withId(countryId).withRegion(regionNode)
                .build(ObjectType.NODE);

        RegionModel regionModelExpected = (RegionModel) regionBuilder.withId(regionId).build(ObjectType.MODEL);
        String pathToRegionLink = REGION_BASE_PATH + "/" + regionId.intValue();
        regionModelExpected.add(new Link(pathToRegionLink));

        CountryModel countryModelExpected = (CountryModel) countryBuilder.withId(countryId).withRegion(regionModelExpected)
                .build(ObjectType.MODEL);

        String pathToCountryLink = COUNTRY_BASE_PATH + "/" + countryId.intValue();

        when(modelMapper.map(countryNode, CountryModel.class)).thenReturn(countryModelExpected);
        when(regionModelAssembler.toModel(regionNode)).thenReturn(regionModelExpected);

        CountryModel countryModelActual = modelAssembler.toModel(countryNode);

        assertAll(
                () -> assertEquals(pathToCountryLink, countryModelActual.getLink("self").get().getHref(),
                        () -> "should return country model with self link: " + pathToCountryLink + ", but was: "
                                + countryModelActual.getLink("self").get().getHref()),
                () -> assertEquals(pathToRegionLink, countryModelActual.getRegion().getLink("self").get().getHref(),
                        () -> "should return country model with region model with self link: " + pathToCountryLink + ", but was: "
                                + countryModelActual.getRegion().getLink("self").get().getHref()),

                () -> assertNotNull(countryModelActual, () -> "should return not null country model, but was: null"),
                () -> assertEquals(countryModelExpected.getId(), countryModelActual.getId(),
                        () -> "should return country model with id: " + countryModelExpected.getId() + ", but was: "
                                + countryModelActual.getId()),
                () -> assertEquals(countryModelExpected.getName(), countryModelActual.getName(),
                        () -> "should return country model with name: " + countryModelExpected.getName() + ", but was: "
                                + countryModelActual.getName()),

                () -> assertNotNull(countryModelExpected.getRegion(),
                        () -> "should return country model with not null region, but was: null"),
                () -> assertEquals(regionModelExpected, countryModelExpected.getRegion(),
                        () -> "should return country model with region: " + countryModelExpected + ", but was: "
                                + countryModelExpected.getRegion()),
                () -> assertEquals(regionModelExpected.getId(), countryModelExpected.getRegion().getId(),
                        () -> "should return country model with region id: " + regionModelExpected.getId()
                                + ", but was: " + countryModelExpected.getRegion().getId()),
                () -> assertEquals(regionModelExpected.getName(), countryModelExpected.getRegion().getName(),
                        () -> "should return country model with region name: " + regionModelExpected.getName() + ", but was: "
                                + countryModelExpected.getRegion().getName()),

                () -> assertFalse(countryModelActual.getLinks().isEmpty(),
                        () -> "should return country model with links, but wasn't"),
                () -> assertFalse(countryModelActual.getRegion().getLinks().isEmpty(),
                        () -> "should return country model with region model with links, but was: " +
                                countryModelActual.getRegion().getLinks()),
                () -> verify(modelMapper, times(1)).map(countryNode, CountryModel.class),
                () -> verifyNoMoreInteractions(modelMapper),
                () -> verify(regionModelAssembler, times(1)).toModel(regionNode),
                () -> verifyNoMoreInteractions(regionModelAssembler));
    }
}
