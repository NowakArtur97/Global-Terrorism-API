package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.RegionBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("ObjectMapper_Tests")
class TargetMapperTest {

    private ObjectMapper objectMapper;

    @Mock
    private ModelMapper modelMapper;

    private static RegionBuilder regionBuilder;
    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        regionBuilder = new RegionBuilder();
        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_target_dto_to_node_should_return_target_node() {

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.withCountry(countryDTOExpected).build(ObjectType.DTO);

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.withId(null).build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withId(null).withRegion(regionNodeExpected)
                .build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(null).withCountry(countryNodeExpected)
                .build(ObjectType.NODE);

        when(modelMapper.map(targetDTOExpected, TargetNode.class)).thenReturn(targetNodeExpected);

        TargetNode targetNodeActual = objectMapper.map(targetDTOExpected, TargetNode.class);

        assertAll(
                () -> assertNull(targetNodeActual.getId(),
                        () -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),

                () -> assertEquals(countryNodeExpected, targetNodeActual.getCountryOfOrigin(),
                        () -> "should return target node with country: " + countryNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                () -> assertEquals(countryNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getId(),
                        () -> "should return target node with country id: " + countryNodeExpected.getId()
                                + ", but was: " + targetNodeActual.getId()),
                () -> assertEquals(countryNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getName(),
                        () -> "should return target node with country name: " + countryNodeExpected.getName()
                                + ", but was: " + targetNodeActual.getCountryOfOrigin()),

                () -> assertEquals(regionNodeExpected, targetNodeActual.getCountryOfOrigin().getRegion(),
                        () -> "should return target node with region: " + regionNodeExpected + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionNodeExpected.getId(), targetNodeActual.getCountryOfOrigin().getRegion().getId(),
                        () -> "should return target node with region id: " + regionNodeExpected.getId()
                                + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionNodeExpected.getName(), targetNodeActual.getCountryOfOrigin().getRegion().getName(),
                        () -> "should return target node with region name: " + regionNodeExpected.getName()
                                + ", but was: " + targetNodeActual.getCountryOfOrigin().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(targetDTOExpected, TargetNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_target_node_to_dto_should_return_target_dto() {

        CountryNode countryNodeExpected = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);

        CountryDTO countryDTOExpected = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.withCountry(countryDTOExpected).build(ObjectType.DTO);

        when(modelMapper.map(targetNodeExpected, TargetDTO.class)).thenReturn(targetDTOExpected);

        TargetDTO targetDTOActual = objectMapper.map(targetNodeExpected, TargetDTO.class);

        assertAll(
                () -> assertEquals(targetDTOExpected.getTarget(), targetDTOActual.getTarget(),
                        () -> "should return target dto with target: " + targetDTOActual.getTarget() + ", but was: "
                                + targetDTOActual.getTarget()),

                () -> assertEquals(countryDTOExpected, targetDTOActual.getCountryOfOrigin(),
                        () -> "should return target dto with country: " + countryDTOExpected + ", but was: " + targetDTOActual.getCountryOfOrigin()),
                () -> assertEquals(countryDTOExpected.getName(), targetDTOActual.getCountryOfOrigin().getName(),
                        () -> "should return target dto with country name: " + countryDTOExpected.getName()
                                + ", but was: " + targetDTOActual.getCountryOfOrigin()),
                () -> verify(modelMapper, times(1)).map(targetNodeExpected, TargetDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_target_node_to_model_should_return_target_model() {

        RegionNode regionNodeExpected = (RegionNode) regionBuilder.build(ObjectType.NODE);
        CountryNode countryNodeExpected = (CountryNode) countryBuilder.withRegion(regionNodeExpected).build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withCountry(countryNodeExpected).build(ObjectType.NODE);

        RegionModel regionModelExpected = (RegionModel) regionBuilder.build(ObjectType.MODEL);
        CountryModel countryModelExpected = (CountryModel) countryBuilder.withRegion(regionModelExpected).build(ObjectType.MODEL);
        TargetModel targetModelExpected = (TargetModel) targetBuilder.withCountry(countryModelExpected).build(ObjectType.MODEL);

        when(modelMapper.map(targetNodeExpected, TargetModel.class)).thenReturn(targetModelExpected);

        TargetModel targetModelActual = objectMapper.map(targetNodeExpected, TargetModel.class);

        assertAll(
                () -> assertEquals(targetModelExpected.getId(), targetModelActual.getId(),
                        () -> "should return target model with id: " + countryModelExpected.getId()
                                + ", but was: " + targetModelActual.getId()),
                () -> assertEquals(targetModelExpected.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetModelExpected.getTarget() + ", but was: "
                                + targetModelActual.getTarget()),

                () -> assertEquals(countryModelExpected, targetModelActual.getCountryOfOrigin(),
                        () -> "should return target model with country: " + countryModelExpected + ", but was: " + targetModelActual.getCountryOfOrigin()),
                () -> assertEquals(countryModelExpected.getId(), targetModelActual.getCountryOfOrigin().getId(),
                        () -> "should return target model with country id: " + countryModelExpected.getId()
                                + ", but was: " + targetModelActual.getId()),
                () -> assertEquals(countryModelExpected.getName(), targetModelActual.getCountryOfOrigin().getName(),
                        () -> "should return target model with country name: " + countryModelExpected.getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin()),

                () -> assertEquals(regionModelExpected, targetModelActual.getCountryOfOrigin().getRegion(),
                        () -> "should return target model with region: " + regionModelExpected + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion()),
                () -> assertEquals(regionModelExpected.getId(), targetModelActual.getCountryOfOrigin().getRegion().getId(),
                        () -> "should return target model with region id: " + regionModelExpected.getId()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion().getId()),
                () -> assertEquals(regionModelExpected.getName(), targetModelActual.getCountryOfOrigin().getRegion().getName(),
                        () -> "should return target model with region name: " + regionModelExpected.getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin().getRegion().getName()),
                () -> verify(modelMapper, times(1)).map(targetNodeExpected, TargetModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}