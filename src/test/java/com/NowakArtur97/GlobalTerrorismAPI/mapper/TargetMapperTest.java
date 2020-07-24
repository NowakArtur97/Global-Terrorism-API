package com.NowakArtur97.GlobalTerrorismAPI.mapper;

import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
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

    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
    }

    @BeforeEach
    private void setUp() {

        objectMapper = new ObjectMapperImpl(modelMapper);
    }

    @Test
    void when_map_target_dto_to_node_should_return_target_node() {

        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNodeExpected = (TargetNode) targetBuilder.withId(null).withCountry(countryNode).build(ObjectType.NODE);

        when(modelMapper.map(targetDTO, TargetNode.class)).thenReturn(targetNodeExpected);

        TargetNode targetNodeActual = objectMapper.map(targetDTO, TargetNode.class);

        assertAll(
                () -> assertNull(targetNodeActual.getId(),
                        () -> "should return target node with id as null, but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getTarget(), targetNodeActual.getTarget(),
                        () -> "should return target node with target: " + targetNodeExpected.getTarget() + ", but was: "
                                + targetNodeActual.getTarget()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin(), targetNodeActual.getCountryOfOrigin(),
                        () -> "should return target node with country: " + targetNodeExpected.getCountryOfOrigin() + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin().getId(), targetNodeActual.getCountryOfOrigin().getId(),
                        () -> "should return target node with country id: " + targetNodeExpected.getCountryOfOrigin().getId()
                                + ", but was: " + targetNodeActual.getId()),
                () -> assertEquals(targetNodeExpected.getCountryOfOrigin().getName(), targetNodeActual.getCountryOfOrigin().getName(),
                        () -> "should return target node with country name: " + targetNodeExpected.getCountryOfOrigin().getName()
                                + ", but was: " + targetNodeActual.getCountryOfOrigin()),
                () -> verify(modelMapper, times(1)).map(targetDTO, TargetNode.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_target_node_to_dto_should_return_target_dto() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CountryDTO countryDTO = (CountryDTO) countryBuilder.build(ObjectType.DTO);
        TargetDTO targetDTOExpected = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);

        when(modelMapper.map(targetNode, TargetDTO.class)).thenReturn(targetDTOExpected);

        TargetDTO targetDTOActual = objectMapper.map(targetNode, TargetDTO.class);

        assertAll(
                () -> assertEquals(targetDTOExpected.getTarget(), targetDTOActual.getTarget(),
                        () -> "should return target dto with target: " + targetDTOActual.getTarget() + ", but was: "
                                + targetDTOActual.getTarget()),
                () -> assertEquals(targetDTOExpected.getCountryOfOrigin(), targetDTOActual.getCountryOfOrigin(),
                        () -> "should return target dto with country: " + targetDTOExpected.getCountryOfOrigin() + ", but was: " + targetDTOActual.getCountryOfOrigin()),
                () -> assertEquals(targetDTOExpected.getCountryOfOrigin().getName(), targetDTOActual.getCountryOfOrigin().getName(),
                        () -> "should return target dto with country name: " + targetDTOExpected.getCountryOfOrigin().getName()
                                + ", but was: " + targetDTOActual.getCountryOfOrigin()),
                () -> verify(modelMapper, times(1)).map(targetNode, TargetDTO.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }

    @Test
    void when_map_target_node_to_model_should_return_target_model() {

        CountryNode countryNode = (CountryNode) countryBuilder.build(ObjectType.NODE);
        TargetNode targetNode = (TargetNode) targetBuilder.withCountry(countryNode).build(ObjectType.NODE);
        CountryModel countryModel = (CountryModel) countryBuilder.build(ObjectType.MODEL);
        TargetModel targetModelExpected = (TargetModel) targetBuilder.withCountry(countryModel).build(ObjectType.MODEL);

        when(modelMapper.map(targetNode, TargetModel.class)).thenReturn(targetModelExpected);

        TargetModel targetModelActual = objectMapper.map(targetNode, TargetModel.class);

        assertAll(
                () -> assertEquals(targetModelExpected.getTarget(), targetModelActual.getTarget(),
                        () -> "should return target model with target: " + targetModelExpected.getTarget() + ", but was: "
                                + targetModelActual.getTarget()),
                () -> assertEquals(targetModelExpected.getCountryOfOrigin(), targetModelActual.getCountryOfOrigin(),
                        () -> "should return target model with country: " + targetModelExpected.getCountryOfOrigin() + ", but was: " + targetModelActual.getCountryOfOrigin()),
                () -> assertEquals(targetModelExpected.getCountryOfOrigin().getId(), targetModelActual.getCountryOfOrigin().getId(),
                        () -> "should return target model with country id: " + targetModelExpected.getCountryOfOrigin().getId()
                                + ", but was: " + targetModelActual.getId()),
                () -> assertEquals(targetModelExpected.getCountryOfOrigin().getName(), targetModelActual.getCountryOfOrigin().getName(),
                        () -> "should return target model with country name: " + targetModelExpected.getCountryOfOrigin().getName()
                                + ", but was: " + targetModelActual.getCountryOfOrigin()),
                () -> verify(modelMapper, times(1)).map(targetNode, TargetModel.class),
                () -> verifyNoMoreInteractions(modelMapper));
    }
}