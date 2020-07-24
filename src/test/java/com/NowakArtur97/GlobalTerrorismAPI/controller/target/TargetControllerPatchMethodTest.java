package com.NowakArtur97.GlobalTerrorismAPI.controller.target;

import com.NowakArtur97.GlobalTerrorismAPI.advice.GenericRestControllerAdvice;
import com.NowakArtur97.GlobalTerrorismAPI.advice.RestResponseGlobalEntityExceptionHandler;
import com.NowakArtur97.GlobalTerrorismAPI.assembler.TargetModelAssembler;
import com.NowakArtur97.GlobalTerrorismAPI.controller.GenericRestController;
import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonMergePatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.httpMessageConverter.JsonPatchHttpMessageConverter;
import com.NowakArtur97.GlobalTerrorismAPI.mediaType.PatchMediaType;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.CountryModel;
import com.NowakArtur97.GlobalTerrorismAPI.model.response.TargetModel;
import com.NowakArtur97.GlobalTerrorismAPI.node.CountryNode;
import com.NowakArtur97.GlobalTerrorismAPI.node.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.repository.CountryRepository;
import com.NowakArtur97.GlobalTerrorismAPI.service.api.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
import com.NowakArtur97.GlobalTerrorismAPI.util.patch.PatchHelper;
import com.NowakArtur97.GlobalTerrorismAPI.util.violation.ViolationHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.json.JsonMergePatch;
import javax.json.JsonPatch;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(NameWithSpacesGenerator.class)
@Tag("TargetController_Tests")
class TargetControllerPatchMethodTest {

    private final String BASE_PATH = "http://localhost:8080/api/v1/targets";

    private MockMvc mockMvc;

    private GenericRestController<TargetModel, TargetDTO> targetController;

    private RestResponseGlobalEntityExceptionHandler restResponseGlobalEntityExceptionHandler;

    @Mock
    private GenericService<TargetNode, TargetDTO> targetService;

    @Mock
    private TargetModelAssembler targetModelAssembler;

    @Mock
    private PagedResourcesAssembler<TargetNode> pagedResourcesAssembler;

    @Mock
    private PatchHelper patchHelper;

    @Autowired
    private ViolationHelper<TargetNode, TargetDTO> violationHelper;

    private static CountryBuilder countryBuilder;
    private static TargetBuilder targetBuilder;

    private static CountryNode country = new CountryNode("updated country");

    @BeforeAll
    private static void setUpBuilders() {

        countryBuilder = new CountryBuilder();
        targetBuilder = new TargetBuilder();
    }

    @BeforeAll
    private static void setUpCountry(@Autowired CountryRepository countryRepository) {

        countryRepository.save(country);
    }

    @AfterAll
    private static void tearDown(@Autowired CountryRepository countryRepository) {

        countryRepository.delete(country);
    }

    @BeforeEach
    private void setUp() {

        targetController = new TargetController(targetService, targetModelAssembler, pagedResourcesAssembler,
                patchHelper, violationHelper);

        restResponseGlobalEntityExceptionHandler = new RestResponseGlobalEntityExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(targetController, restResponseGlobalEntityExceptionHandler)
                .setMessageConverters(new JsonMergePatchHttpMessageConverter(), new JsonPatchHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter())
                .setControllerAdvice(new GenericRestControllerAdvice())
                .build();
    }

    @Test
    void when_partial_update_valid_target_using_json_patch_should_return_partially_updated_node() {

        Long targetId = 1L;
        Long countryId = 2L;
        String updatedTargetName = "updated target";
        String updatedCountryName = "updated country";

        CountryNode countryNode = (CountryNode) countryBuilder.withId(countryId).build(ObjectType.NODE);
        CountryNode updatedCountryNode = (CountryNode) countryBuilder.withId(countryId).withName(updatedCountryName)
                .build(ObjectType.NODE);
        CountryModel countryModel = (CountryModel) countryBuilder.withId(countryId).withName(updatedCountryName)
                .build(ObjectType.MODEL);

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).withCountry(countryNode)
                .build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withId(targetId).withTarget(updatedTargetName)
                .withCountry(updatedCountryNode)
                .build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).withTarget(updatedTargetName)
                .withCountry(countryModel)
                .build(ObjectType.MODEL);

        String pathToLink = BASE_PATH + "/" + targetId.intValue();
        Link link = new Link(pathToLink);
        targetModel.add(link);

        String linkWithParameter = BASE_PATH + "/" + "{id}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(patchHelper.patch(any(JsonPatch.class), eq(targetNode), ArgumentMatchers.any()))
                .thenReturn(updatedTargetNode);
        when(targetService.save(updatedTargetNode)).thenReturn(updatedTargetNode);
        when(targetModelAssembler.toModel(updatedTargetNode)).thenReturn(targetModel);

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + updatedTargetName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": \"" + updatedCountryName + "\" }]";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink)))
                        .andExpect(jsonPath("id", is(targetId.intValue())))
                        .andExpect(jsonPath("target", is(updatedTargetName)))
                        .andExpect(jsonPath("countryOfOrigin.id", is(countryId.intValue())))
                        .andExpect(jsonPath("countryOfOrigin.name", is(updatedCountryName)))
                        .andExpect(jsonPath("countryOfOrigin.links").isEmpty()),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class),
                        eq(targetNode), ArgumentMatchers.<Class<TargetNode>>any()),
                () -> verifyNoMoreInteractions(patchHelper),
                () -> verify(targetService, times(1)).save(updatedTargetNode),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(targetModelAssembler, times(1)).toModel(updatedTargetNode),
                () -> verifyNoMoreInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_partial_update_target_using_json_patch_but_target_not_exists_should_return_error_response() {

        Long targetId = 1L;

        String updatedTargetName = "updated target";
        String updatedCountryName = "updated country";

        String linkWithParameter = BASE_PATH + "/" + "{id}";

        when(targetService.findById(targetId)).thenReturn(Optional.empty());

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + updatedTargetName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": \"" + updatedCountryName + "\" }]";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]",
                                is("Could not find TargetModel with id: " + targetId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @ParameterizedTest(name = "{index}: Target Name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_invalid_target_using_json_patch_should_return_errors(String invalidTargetName) {

        Long targetId = 1L;
        String updatedCountryName = "updated country";

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        CountryNode updatedCountryNode = (CountryNode) countryBuilder.withName(updatedCountryName)
                .build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withId(targetId)
                .withTarget(invalidTargetName).withCountry(updatedCountryNode)
                .build(ObjectType.NODE);

        String linkWithParameter = BASE_PATH + "/" + "{id}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(patchHelper.patch(any(JsonPatch.class), eq(targetNode),
                ArgumentMatchers.any())).thenReturn(updatedTargetNode);

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + invalidTargetName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": \"" + updatedCountryName + "\" }]";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class), eq(targetNode), ArgumentMatchers.<Class<TargetNode>>any()),
                () -> verifyNoMoreInteractions(patchHelper),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_partial_update_target_with_country_as_null_using_json_patch_should_return_errors() {

        Long targetId = 1L;
        String updatedTargetName = "updated target";

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withId(targetId)
                .withTarget(updatedTargetName).withCountry(null).build(ObjectType.NODE);

        String linkWithParameter = BASE_PATH + "/" + "{id}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(patchHelper.patch(any(JsonPatch.class), eq(targetNode),
                ArgumentMatchers.any())).thenReturn(updatedTargetNode);

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + updatedTargetName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": \"" + null + "\" }]";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Country name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class), eq(targetNode), ArgumentMatchers.<Class<TargetNode>>any()),
                () -> verifyNoMoreInteractions(patchHelper),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_partial_update_valid_target_with_not_existing_country_using_json_patch_should_return_errors() {

        Long targetId = 1L;
        String updatedTargetName = "updated target";
        String notExistingCountryName = "not existing country";

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        CountryNode notExistingCountry = (CountryNode) countryBuilder.withName(notExistingCountryName)
                .build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withId(targetId)
                .withTarget(updatedTargetName).withCountry(notExistingCountry).build(ObjectType.NODE);

        String linkWithParameter = BASE_PATH + "/" + "{id}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(patchHelper.patch(any(JsonPatch.class), eq(targetNode),
                ArgumentMatchers.any())).thenReturn(updatedTargetNode);

        String jsonPatch = "[" +
                "{ \"op\": \"replace\", \"path\": \"/target\", \"value\": \"" + updatedTargetName + "\" }," +
                "{ \"op\": \"replace\", \"path\": \"/countryOfOrigin/name\", \"value\": \"" + notExistingCountry + "\" }]";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonPatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(patchHelper, times(1)).patch(any(JsonPatch.class), eq(targetNode), ArgumentMatchers.<Class<TargetNode>>any()),
                () -> verifyNoMoreInteractions(patchHelper),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_partial_update_valid_target_using_json_merge_patch_should_return_partially_updated_node() {

        Long targetId = 1L;
        Long countryId = 1L;
        String updatedTargetName = "updated target";
        String updatedCountryName = "updated country";
        CountryNode countryNode = (CountryNode) countryBuilder.withId(countryId).build(ObjectType.NODE);
        CountryNode updatedCountryNode = (CountryNode) countryBuilder.withId(countryId).withName(updatedCountryName)
                .build(ObjectType.NODE);
        CountryModel countryModel = (CountryModel) countryBuilder.withId(countryId).withName(updatedCountryName)
                .build(ObjectType.MODEL);

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).withCountry(countryNode)
                .build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withId(targetId).withTarget(updatedTargetName)
                .withCountry(updatedCountryNode)
                .build(ObjectType.NODE);
        TargetModel targetModel = (TargetModel) targetBuilder.withId(targetId).withTarget(updatedTargetName)
                .withCountry(countryModel)
                .build(ObjectType.MODEL);

        String pathToLink = BASE_PATH + "/" + targetId.intValue();
        Link link = new Link(pathToLink);
        targetModel.add(link);

        String linkWithParameter = BASE_PATH + "/" + "{id2}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(patchHelper.mergePatch(any(JsonMergePatch.class), eq(targetNode),
                ArgumentMatchers.any())).thenReturn(updatedTargetNode);
        when(targetService.save(updatedTargetNode)).thenReturn(updatedTargetNode);
        when(targetModelAssembler.toModel(updatedTargetNode)).thenReturn(targetModel);

        String jsonMergePatch = "{ \"target\": \"" + updatedTargetName + "\", " +
                "\"countryOfOrigin/name\": \"" + updatedCountryName + "\" }";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("links[0].href", is(pathToLink)))
                        .andExpect(jsonPath("id", is(targetId.intValue())))
                        .andExpect(jsonPath("target", is(updatedTargetName)))
                        .andExpect(jsonPath("countryOfOrigin.id", is(countryId.intValue())))
                        .andExpect(jsonPath("countryOfOrigin.name", is(updatedCountryName)))
                        .andExpect(jsonPath("countryOfOrigin.links").isEmpty()),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class), eq(targetNode), ArgumentMatchers.<Class<TargetNode>>any()),
                () -> verifyNoMoreInteractions(patchHelper),
                () -> verify(targetService, times(1)).save(updatedTargetNode),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(targetModelAssembler, times(1)).toModel(updatedTargetNode),
                () -> verifyNoMoreInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_partial_update_target_using_json_merge_patch_but_target_not_exists_should_return_error_response() {

        Long targetId = 1L;

        String linkWithParameter = BASE_PATH + "/" + "{id2}";

        String updatedTargetName = "updated target";
        String updatedCountryName = "updated country";

        when(targetService.findById(targetId)).thenReturn(Optional.empty());

        String jsonMergePatch = "{ \"target\": \"" + updatedTargetName + "\", " +
                "\"countryOfOrigin/name\": \"" + updatedCountryName + "\" }";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(404)))
                        .andExpect(jsonPath("errors[0]", is("Could not find TargetModel with id: " + targetId + ".")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verifyNoInteractions(patchHelper),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @ParameterizedTest(name = "{index}: Target Name: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void when_partial_update_invalid_target_using_json_merge_patch_should_return_errors(String invalidTargetName) {

        Long targetId = 1L;
        String updatedCountryName = "updated country";

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        CountryNode updatedCountryNode = (CountryNode) countryBuilder.withName(updatedCountryName)
                .build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withId(targetId)
                .withTarget(invalidTargetName).withCountry(updatedCountryNode)
                .build(ObjectType.NODE);

        String linkWithParameter = BASE_PATH + "/" + "{id2}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(patchHelper.mergePatch(any(JsonMergePatch.class), eq(targetNode),
                ArgumentMatchers.any())).thenReturn(updatedTargetNode);

        String jsonMergePatch = "{ \"target\": \"" + invalidTargetName + "\", " +
                "\"countryOfOrigin/name\": \"" + updatedCountryName + "\" }";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class), eq(targetNode), ArgumentMatchers.<Class<TargetNode>>any()),
                () -> verifyNoMoreInteractions(patchHelper),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_partial_update_target_with_country_as_null_using_json_merge_patch_should_return_errors() {

        Long targetId = 1L;
        String updatedTargetName = "updated target";

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withId(targetId)
                .withTarget(updatedTargetName).withCountry(null).build(ObjectType.NODE);

        String linkWithParameter = BASE_PATH + "/" + "{id2}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(patchHelper.mergePatch(any(JsonMergePatch.class), eq(targetNode),
                ArgumentMatchers.any())).thenReturn(updatedTargetNode);

        String jsonMergePatch = "{ \"target\": \"" + updatedTargetName + "\", " +
                "\"countryOfOrigin/name\": \"" + null + "\" }";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("Country name cannot be empty.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class), eq(targetNode), ArgumentMatchers.<Class<TargetNode>>any()),
                () -> verifyNoMoreInteractions(patchHelper),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }

    @Test
    void when_partial_update_valid_target_with_not_existing_country_using_json_merge_patch_should_return_errors() {

        Long targetId = 1L;
        String updatedTargetName = "updated target";
        String notExistingCountryName = "not existing country";

        TargetNode targetNode = (TargetNode) targetBuilder.withId(targetId).build(ObjectType.NODE);
        CountryNode notExistingCountry = (CountryNode) countryBuilder.withName(notExistingCountryName)
                .build(ObjectType.NODE);
        TargetNode updatedTargetNode = (TargetNode) targetBuilder.withId(targetId)
                .withTarget(updatedTargetName).withCountry(notExistingCountry).build(ObjectType.NODE);

        String linkWithParameter = BASE_PATH + "/" + "{id2}";

        when(targetService.findById(targetId)).thenReturn(Optional.of(targetNode));
        when(patchHelper.mergePatch(any(JsonMergePatch.class), eq(targetNode),
                ArgumentMatchers.any())).thenReturn(updatedTargetNode);

        String jsonMergePatch = "{ \"target\": \"" + updatedTargetName + "\", " +
                "\"countryOfOrigin/name\": \"" + notExistingCountry + "\" }";

        assertAll(
                () -> mockMvc
                        .perform(patch(linkWithParameter, targetId).content(jsonMergePatch)
                                .contentType(PatchMediaType.APPLICATION_JSON_MERGE_PATCH))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("timestamp", is(notNullValue())))
                        .andExpect(jsonPath("status", is(400)))
                        .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
                        .andExpect(jsonPath("errors", hasSize(1))),
                () -> verify(targetService, times(1)).findById(targetId),
                () -> verifyNoMoreInteractions(targetService),
                () -> verify(patchHelper, times(1)).mergePatch(any(JsonMergePatch.class), eq(targetNode), ArgumentMatchers.<Class<TargetNode>>any()),
                () -> verifyNoMoreInteractions(patchHelper),
                () -> verifyNoInteractions(targetModelAssembler),
                () -> verifyNoInteractions(pagedResourcesAssembler));
    }
}