//package com.NowakArtur97.GlobalTerrorismAPI.controller.group;
//
//import com.NowakArtur97.GlobalTerrorismAPI.dto.CountryDTO;
//import com.NowakArtur97.GlobalTerrorismAPI.dto.EventDTO;
//import com.NowakArtur97.GlobalTerrorismAPI.dto.GroupDTO;
//import com.NowakArtur97.GlobalTerrorismAPI.dto.TargetDTO;
//import com.NowakArtur97.GlobalTerrorismAPI.node.*;
//import com.NowakArtur97.GlobalTerrorismAPI.repository.*;
//import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.CountryBuilder;
//import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.EventBuilder;
//import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.GroupBuilder;
//import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.TargetBuilder;
//import com.NowakArtur97.GlobalTerrorismAPI.testUtil.builder.enums.ObjectType;
//import com.NowakArtur97.GlobalTerrorismAPI.testUtil.mapper.ObjectTestMapper;
//import com.NowakArtur97.GlobalTerrorismAPI.testUtil.nameGenerator.NameWithSpacesGenerator;
//import com.NowakArtur97.GlobalTerrorismAPI.util.jwt.JwtUtil;
//import org.hamcrest.CoreMatchers;
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.EmptySource;
//import org.junit.jupiter.params.provider.NullAndEmptySource;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//import static org.hamcrest.CoreMatchers.hasItem;
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
//import static org.hamcrest.core.IsNull.notNullValue;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@AutoConfigureMockMvc
//@DisplayNameGeneration(NameWithSpacesGenerator.class)
//@Tag("GroupController_Tests")
//class GroupControllerPutMethodTest {
//
//    private final String GROUP_BASE_PATH = "http://localhost:8080/api/v1/groups";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    private static CountryBuilder countryBuilder;
//    private static TargetBuilder targetBuilder;
//    private static EventBuilder eventBuilder;
//    private static GroupBuilder groupBuilder;
//
//    private final static UserNode userNode = new UserNode("user1234", "Password1234!", "user1234email@.com",
//            Set.of(new RoleNode("user")));
//
//    private final static CountryNode countryNode = new CountryNode("country");
//
//    private final static TargetNode targetNode = new TargetNode("target", countryNode);
//    private final static TargetNode targetNode2 = new TargetNode("target 2", countryNode);
//
//    private final static EventNode eventNode = new EventNode("summary", "motive", new Date(), true, true, true, targetNode);
//    private final static EventNode eventNode2 = new EventNode("summary 2", "motive 2", new Date(), false, false, false, targetNode2);
//
//    private final static GroupNode groupNode = new GroupNode("group", List.of(eventNode, eventNode2));
//
//    @BeforeAll
//    private static void setUpBuilders() {
//
//        countryBuilder = new CountryBuilder();
//        targetBuilder = new TargetBuilder();
//        eventBuilder = new EventBuilder();
//        groupBuilder = new GroupBuilder();
//    }
//
//    @BeforeAll
//    private static void setUp(@Autowired UserRepository userRepository, @Autowired GroupRepository groupRepository) {
//
//        userRepository.save(userNode);
//
//        groupRepository.save(groupNode);
//    }
//
//    @AfterAll
//    private static void tearDown(@Autowired UserRepository userRepository, @Autowired GroupRepository groupRepository,
//                                 @Autowired EventRepository eventRepository, @Autowired TargetRepository targetRepository,
//                                 @Autowired CountryRepository countryRepository, @Autowired CityRepository cityRepository) {
//
//        userRepository.deleteAll();
//
//        cityRepository.deleteAll();
//
//        countryRepository.deleteAll();
//
//        groupRepository.deleteAll();
//
//        eventRepository.deleteAll();
//
//        targetRepository.deleteAll();
//    }
//
//    @Test
//    void when_update_valid_group_should_return_updated_group_as_model() {
//
//        String updatedGroupName = "new group name";
//
//        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
//        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(targetNode.getTarget()).withCountry(countryDTO).build(ObjectType.DTO);
//        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget(targetNode2.getTarget()).withCountry(countryDTO).build(ObjectType.DTO);
//        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(eventNode.getSummary()).withMotive(eventNode.getMotive())
//                .withDate(eventNode.getDate()).withIsPartOfMultipleIncidents(eventNode.getIsPartOfMultipleIncidents())
//                .withIsSuccessful(eventNode.getIsSuccessful()).withIsSuicidal(eventNode.getIsSuicidal()).withTarget(targetDTO)
//                .build(ObjectType.DTO);
//        EventDTO eventDTO2 = (EventDTO) eventBuilder.withSummary(eventNode2.getSummary()).withMotive(eventNode2.getMotive())
//                .withDate(eventNode2.getDate()).withIsPartOfMultipleIncidents(eventNode2.getIsPartOfMultipleIncidents())
//                .withIsSuccessful(eventNode2.getIsSuccessful()).withIsSuicidal(eventNode2.getIsSuicidal()).withTarget(targetDTO2)
//                .build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO, eventDTO2))
//                .build(ObjectType.DTO);
//
//        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
//        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isOk())
//                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
//                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
//                        .andExpect(jsonPath("id", is(groupNode.getId().intValue())))
//                        .andExpect(jsonPath("name", is(groupDTO.getName())))
//                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
//                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
//                        .andExpect(jsonPath("eventsCaused[0].date",
//                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
//                                                .toLocalDate()))))
//                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
//                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
//                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
//                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
//                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
//                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
//                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
//                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
//                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
//                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
//                        .andExpect(jsonPath("eventsCaused[1].date",
//                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
//                                                .toLocalDate()))))
//                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
//                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
//                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventDTO2.getIsPartOfMultipleIncidents())))
//                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
//                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
//                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
//                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
//                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
//                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
//    }
//
//    @Test
//    void when_update_valid_group_with_events_should_return_updated_group_as_model() throws ParseException {
//
//        String updatedTargetName = "new target name";
//
//        String updatedSummary = "summary updated";
//        String updatedMotive = "motive updated";
//        Date updatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS").parse("01/08/2010 02:00:00:000");
//        boolean updatedIsPartOfMultipleIncidents = false;
//        boolean updatedIsSuccessful = false;
//        boolean updatedIsSuicidal = false;
//
//        String updatedGroupName = "new group name";
//
//        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
//        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(updatedTargetName).withCountry(countryDTO).build(ObjectType.DTO);
//        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget(updatedTargetName + " 2").withCountry(countryDTO)
//                .build(ObjectType.DTO);
//        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(updatedSummary + " 2").withMotive(updatedMotive + " 2")
//                .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
//                .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal).withTarget(targetDTO)
//                .build(ObjectType.DTO);
//        EventDTO eventDTO2 = (EventDTO) eventBuilder.withSummary(updatedSummary).withMotive(updatedMotive)
//                .withDate(updatedDate).withIsPartOfMultipleIncidents(updatedIsPartOfMultipleIncidents)
//                .withIsSuccessful(updatedIsSuccessful).withIsSuicidal(updatedIsSuicidal).withTarget(targetDTO2)
//                .build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(updatedGroupName).withEventsCaused(List.of(eventDTO, eventDTO2))
//                .build(ObjectType.DTO);
//
//        String pathToGroupLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue();
//        String pathToEventsLink = GROUP_BASE_PATH + "/" + groupNode.getId().intValue() + "/events";
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isOk())
//                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("links[0].href", is(pathToGroupLink)))
//                        .andExpect(jsonPath("links[1].href", is(pathToEventsLink)))
//                        .andExpect(jsonPath("id", is(groupNode.getId().intValue())))
//                        .andExpect(jsonPath("name", is(groupDTO.getName())))
//                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
//                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
//                        .andExpect(jsonPath("eventsCaused[0].date",
//                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
//                                                .toLocalDate()))))
//                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
//                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
//                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
//                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
//                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
//                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
//                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
//                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
//
//                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
//                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
//                        .andExpect(jsonPath("eventsCaused[1].date",
//                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
//                                                .toLocalDate()))))
//                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
//                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
//                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventDTO2.getIsPartOfMultipleIncidents())))
//                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
//                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
//                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
//                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
//                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
//                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
//    }
//
//    @Test
//    void when_update_valid_group_with_not_existing_id_should_return_new_group_as_model() {
//
//        Long notExistingId = 10000L;
//
//        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
//        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
//        TargetDTO targetDTO2 = (TargetDTO) targetBuilder.withTarget("target 2").withCountry(countryDTO).build(ObjectType.DTO);
//        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
//        EventDTO eventDTO2 = (EventDTO) eventBuilder.withMotive("motive 2").withSummary("summary 2")
//                .withIsSuicidal(false).withIsSuccessful(false).withIsPartOfMultipleIncidents(false)
//                .withTarget(targetDTO2).build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO, eventDTO2)).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, notExistingId)
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isCreated())
//                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("links[0].href", notNullValue()))
//                        .andExpect(jsonPath("links[1].href", notNullValue()))
//                        .andExpect(jsonPath("id", notNullValue()))
//                        .andExpect(jsonPath("name", is(groupDTO.getName())))
//                        .andExpect(jsonPath("eventsCaused[0].links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].links[1].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].summary", is(eventDTO.getSummary())))
//                        .andExpect(jsonPath("eventsCaused[0].motive", is(eventDTO.getMotive())))
//                        .andExpect(jsonPath("eventsCaused[0].date",
//                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                                        .format(eventDTO.getDate().toInstant().atZone(ZoneId.systemDefault())
//                                                .toLocalDate()))))
//                        .andExpect(jsonPath("eventsCaused[0].isSuicidal", is(eventDTO.getIsSuicidal())))
//                        .andExpect(jsonPath("eventsCaused[0].isSuccessful", is(eventDTO.getIsSuccessful())))
//                        .andExpect(jsonPath("eventsCaused[0].isPartOfMultipleIncidents", is(eventDTO.getIsPartOfMultipleIncidents())))
//                        .andExpect(jsonPath("eventsCaused[0].target.links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].target.links[1].href").doesNotExist())
//                        .andExpect(jsonPath("eventsCaused[0].target.id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[0].target.target", is(targetDTO.getTarget())))
//                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
//                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.name", is(countryNode.getName())))
//                        .andExpect(jsonPath("eventsCaused[0].target.countryOfOrigin.links").isEmpty())
//                        .andExpect(jsonPath("eventsCaused[1].links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].links[1].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].summary", is(eventDTO2.getSummary())))
//                        .andExpect(jsonPath("eventsCaused[1].motive", is(eventDTO2.getMotive())))
//                        .andExpect(jsonPath("eventsCaused[1].date",
//                                is(DateTimeFormatter.ofPattern("yyyy-MM-dd")
//                                        .format(eventDTO2.getDate().toInstant().atZone(ZoneId.systemDefault())
//                                                .toLocalDate()))))
//                        .andExpect(jsonPath("eventsCaused[1].isSuicidal", is(eventDTO2.getIsSuicidal())))
//                        .andExpect(jsonPath("eventsCaused[1].isSuccessful", is(eventDTO2.getIsSuccessful())))
//                        .andExpect(jsonPath("eventsCaused[1].isPartOfMultipleIncidents", is(eventDTO2.getIsPartOfMultipleIncidents())))
//                        .andExpect(jsonPath("eventsCaused[1].target.links[0].href", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].target.links[1].href").doesNotExist())
//                        .andExpect(jsonPath("eventsCaused[1].target.id", notNullValue()))
//                        .andExpect(jsonPath("eventsCaused[1].target.target", is(targetDTO2.getTarget())))
//                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.id", is(countryNode.getId().intValue())))
//                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.name", is(countryNode.getName())))
//                        .andExpect(jsonPath("eventsCaused[1].target.countryOfOrigin.links").isEmpty())
//                        .andExpect(jsonPath("eventsCaused[2]").doesNotExist()));
//    }
//
//    @Test
//    void when_update_group_with_null_fields_should_return_errors() {
//
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(null).withEventsCaused(null).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("timestamp", is(notNullValue())))
//                        .andExpect(jsonPath("status", is(400)))
//                        .andExpect(jsonPath("errors", hasItem("Group name cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasItem("List of Events caused by the Group cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasSize(2))));
//    }
//
//    @Test
//    void when_update_group_with_null_event_fields_should_return_errors() {
//
//        EventDTO eventDTO = (EventDTO) eventBuilder.withId(null).withSummary(null).withMotive(null).withDate(null)
//                .withIsPartOfMultipleIncidents(null).withIsSuccessful(null).withIsSuicidal(null).withTarget(null)
//                .build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("timestamp", is(notNullValue())))
//                        .andExpect(jsonPath("status", is(400)))
//                        .andExpect(jsonPath("errors", hasItem("Event summary cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasItem("Event motive cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasItem("Event date cannot be null.")))
//                        .andExpect(jsonPath("errors", hasItem("Event must have information on whether it has been part of many incidents.")))
//                        .andExpect(jsonPath("errors", hasItem("Event must have information about whether it was successful.")))
//                        .andExpect(jsonPath("errors", hasItem("Event must have information about whether it was a suicidal attack.")))
//                        .andExpect(jsonPath("errors", hasItem("Target name cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasSize(7))));
//    }
//
//    @Test
//    void when_update_group_with_empty_events_list_should_return_errors() {
//
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(new ArrayList<>()).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("timestamp", is(notNullValue())))
//                        .andExpect(jsonPath("status", is(400)))
//                        .andExpect(jsonPath("errors[0]", is("List of Events caused by the Group cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasSize(1))));
//    }
//
//    @ParameterizedTest(name = "{index}: For Group name: {0} should have violation")
//    @EmptySource
//    @ValueSource(strings = {" ", "\t", "\n"})
//    void when_update_group_with_invalid_name_should_return_errors(String invalidName) {
//
//        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
//        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
//        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withName(invalidName).withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("timestamp", is(notNullValue())))
//                        .andExpect(jsonPath("status", is(400)))
//                        .andExpect(jsonPath("errors[0]", is("Group name cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasSize(1))));
//    }
//
//    @ParameterizedTest(name = "{index}: Group Target Country: {0}")
//    @NullAndEmptySource
//    @ValueSource(strings = {" "})
//    void when_add_group_with_not_existing_country_should_return_errors(String invalidCountryName) {
//
//        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(invalidCountryName).build(ObjectType.DTO);
//        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
//        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("timestamp", is(CoreMatchers.notNullValue())))
//                        .andExpect(jsonPath("status", is(400)))
//                        .andExpect(jsonPath("errors[0]", is("A country with the provided name does not exist.")))
//                        .andExpect(jsonPath("errors", Matchers.hasSize(1))));
//    }
//
//    @ParameterizedTest(name = "{index}: For Group Target: {0} should have violation")
//    @NullAndEmptySource
//    @ValueSource(strings = {" ", "\t", "\n"})
//    void when_update_group_event_with_invalid_target_should_return_errors(String invalidTarget) {
//
//        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
//        TargetDTO targetDTO = (TargetDTO) targetBuilder.withTarget(invalidTarget).withCountry(countryDTO).build(ObjectType.DTO);
//        EventDTO eventDTO = (EventDTO) eventBuilder.withTarget(targetDTO).build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("timestamp", is(notNullValue())))
//                        .andExpect(jsonPath("status", is(400)))
//                        .andExpect(jsonPath("errors[0]", is("Target name cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasSize(1))));
//    }
//
//    @ParameterizedTest(name = "{index}: For Group event summary: {0} should have violation")
//    @EmptySource
//    @ValueSource(strings = {" ", "\t", "\n"})
//    void when_update_group_event_with_invalid_summary_should_return_errors(String invalidSummary) {
//
//        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
//        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
//        EventDTO eventDTO = (EventDTO) eventBuilder.withSummary(invalidSummary).withTarget(targetDTO)
//                .build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("timestamp", is(notNullValue())))
//                        .andExpect(jsonPath("status", is(400)))
//                        .andExpect(jsonPath("errors[0]", is("Event summary cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasSize(1))));
//    }
//
//    @ParameterizedTest(name = "{index}: For Group event motive: {0} should have violation")
//    @EmptySource
//    @ValueSource(strings = {" ", "\t", "\n"})
//    void when_update_group_event_with_invalid_motive_should_return_errors(String invalidMotive) {
//
//        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
//        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
//        EventDTO eventDTO = (EventDTO) eventBuilder.withMotive(invalidMotive).withTarget(targetDTO)
//                .build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("timestamp", is(notNullValue())))
//                        .andExpect(jsonPath("status", is(400)))
//                        .andExpect(jsonPath("errors[0]", is("Event motive cannot be empty.")))
//                        .andExpect(jsonPath("errors", hasSize(1))));
//    }
//
//    @Test
//    void when_update_group_event_with_date_in_the_future_should_return_errors() {
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2090, Calendar.FEBRUARY, 1);
//        Date invalidDate = calendar.getTime();
//        CountryDTO countryDTO = (CountryDTO) countryBuilder.withName(countryNode.getName()).build(ObjectType.DTO);
//        TargetDTO targetDTO = (TargetDTO) targetBuilder.withCountry(countryDTO).build(ObjectType.DTO);
//        EventDTO eventDTO = (EventDTO) eventBuilder.withDate(invalidDate).withTarget(targetDTO).build(ObjectType.DTO);
//        GroupDTO groupDTO = (GroupDTO) groupBuilder.withEventsCaused(List.of(eventDTO)).build(ObjectType.DTO);
//
//        String linkWithParameter = GROUP_BASE_PATH + "/" + "{id}";
//
//        String token = jwtUtil.generateToken(new User(userNode.getUserName(), userNode.getPassword(),
//                List.of(new SimpleGrantedAuthority("user"))));
//
//        assertAll(
//                () -> mockMvc
//                        .perform(put(linkWithParameter, groupNode.getId())
//                                .header("Authorization", "Bearer " + token)
//                                .content(ObjectTestMapper.asJsonString(groupDTO))
//                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("timestamp", is(notNullValue())))
//                        .andExpect(jsonPath("status", is(400)))
//                        .andExpect(jsonPath("errors[0]", is("Event date cannot be in the future.")))
//                        .andExpect(jsonPath("errors", hasSize(1))));
//    }
//}
