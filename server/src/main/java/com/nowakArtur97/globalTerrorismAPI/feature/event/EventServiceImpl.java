package com.nowakArtur97.globalTerrorismAPI.feature.event;

import com.nowakArtur97.globalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.nowakArtur97.globalTerrorismAPI.common.repository.BaseRepository;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericService;
import com.nowakArtur97.globalTerrorismAPI.common.service.GenericServiceImpl;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityNode;
import com.nowakArtur97.globalTerrorismAPI.feature.city.CityService;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetDTO;
import com.nowakArtur97.globalTerrorismAPI.feature.target.TargetNode;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimNode;
import com.nowakArtur97.globalTerrorismAPI.feature.victim.VictimService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class EventServiceImpl extends GenericServiceImpl<EventNode, EventDTO> implements EventService {

    private final GenericService<TargetNode, TargetDTO> targetService;

    private final CityService cityService;

    private final VictimService victimService;

    EventServiceImpl(BaseRepository<EventNode> repository, ModelMapper modelMapper,
                     GenericService<TargetNode, TargetDTO> targetService, CityService cityService,
                     VictimService victimService) {
        super(repository, modelMapper);
        this.targetService = targetService;
        this.cityService = cityService;
        this.victimService = victimService;
    }

    @Override
    public EventNode save(EventNode eventNode) {

        eventNode.setTarget(targetService.save(eventNode.getTarget()));
        eventNode.setVictim(victimService.save(eventNode.getVictim()));

        CityNode cityNode = eventNode.getCity();

        Optional<CityNode> cityNodeOptional = cityService
                .findByNameAndLatitudeAndLongitude(cityNode.getName(), cityNode.getLatitude(), cityNode.getLongitude());

        if (cityNodeOptional.isPresent()) {
            eventNode.setCity(cityNodeOptional.get());
        } else {
            eventNode.setCity(cityService.save(cityNode));
        }

        return repository.save(eventNode);
    }

    @Override
    public EventNode saveNew(EventDTO eventDTO) {

        EventNode eventNode = modelMapper.map(eventDTO, EventNode.class);

        eventNode.setTarget(targetService.saveNew(eventDTO.getTarget()));
        eventNode.setVictim(victimService.saveNew(eventDTO.getVictim()));

        CityDTO cityDTO = eventDTO.getCity();

        Optional<CityNode> cityNodeOptional = cityService
                .findByNameAndLatitudeAndLongitude(cityDTO.getName(), cityDTO.getLatitude(), cityDTO.getLongitude());

        if (cityNodeOptional.isPresent()) {
            eventNode.setCity(cityNodeOptional.get());
        } else {
            eventNode.setCity(cityService.saveNew(cityDTO));
        }

        return repository.save(eventNode);
    }

    @Override
    public EventNode update(EventNode eventNode, EventDTO eventDTO) {

        Long id = eventNode.getId();

        TargetNode updatedTarget = targetService.update(eventNode.getTarget(), eventDTO.getTarget());
        VictimNode updatedVictim = victimService.update(eventNode.getVictim(), eventDTO.getVictim());

        eventNode = modelMapper.map(eventDTO, EventNode.class);

        setEventCityForUpdate(eventNode, eventDTO);

        eventNode.setId(id);
        eventNode.setTarget(updatedTarget);
        eventNode.setVictim(updatedVictim);

        return repository.save(eventNode);
    }

    @Override
    public Optional<EventNode> delete(Long id) {

        Optional<EventNode> eventNodeOptional = findById(id);

        if (eventNodeOptional.isPresent()) {

            EventNode eventNode = eventNodeOptional.get();

            if (eventNode.getTarget() != null) {
                targetService.delete(eventNode.getTarget().getId());
            }

            if (eventNode.getVictim() != null) {
                victimService.delete(eventNode.getVictim().getId());
            }

            repository.delete(eventNode);
        }

        return eventNodeOptional;
    }

    @Override
    public Optional<EventNode> deleteEventTarget(Long id) {

        Optional<EventNode> eventNodeOptional = findById(id);

        if (eventNodeOptional.isPresent()) {
            if (eventNodeOptional.get().getTarget() != null) {
                targetService.delete(eventNodeOptional.get().getTarget().getId());
            } else {
                throw new ResourceNotFoundException("TargetModel");
            }
        }

        return eventNodeOptional;
    }

    @Override
    public EventNode addOrUpdateEventTarget(EventNode eventNode, TargetDTO targetDTO) {

        TargetNode targetNode;

        if (eventNode.getTarget() != null) {

            targetNode = targetService.update(eventNode.getTarget(), targetDTO);

        } else {

            targetNode = targetService.saveNew(targetDTO);
        }

        eventNode.setTarget(targetNode);

        return repository.save(eventNode);
    }

    private void setEventCityForUpdate(EventNode eventNode, EventDTO eventDTO) {

        CityDTO cityDTO = eventDTO.getCity();

        Optional<CityNode> cityNodeOptional = cityService
                .findByNameAndLatitudeAndLongitude(cityDTO.getName(), cityDTO.getLatitude(), cityDTO.getLongitude());

        if (cityNodeOptional.isPresent()) {

            CityNode cityNode = cityNodeOptional.get();

            if (cityNode.getProvince().getName().equals(cityDTO.getProvince().getName())) {
                eventNode.setCity(cityNode);
            } else {
                eventNode.setCity(cityService.update(cityNode, cityDTO));
            }
        } else {
            eventNode.setCity(cityService.saveNew(cityDTO));
        }
    }
}