package com.NowakArtur97.GlobalTerrorismAPI.feature.event;

import com.NowakArtur97.GlobalTerrorismAPI.common.exception.ResourceNotFoundException;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityNode;
import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityService;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetDTO;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetNode;
import com.NowakArtur97.GlobalTerrorismAPI.common.repository.BaseRepository;
import com.NowakArtur97.GlobalTerrorismAPI.common.service.GenericService;
import com.NowakArtur97.GlobalTerrorismAPI.common.service.GenericServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class EventServiceImpl extends GenericServiceImpl<EventNode, EventDTO> implements EventService {

    private final GenericService<TargetNode, TargetDTO> targetService;

    private final CityService cityService;

    EventServiceImpl(BaseRepository<EventNode> repository, ModelMapper modelMapper,
                     GenericService<TargetNode, TargetDTO> targetService, CityService cityService) {
        super(repository, modelMapper);
        this.targetService = targetService;
        this.cityService = cityService;
    }

    @Override
    public EventNode save(EventNode eventNode) {

        eventNode.setTarget(targetService.save(eventNode.getTarget()));

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

        eventNode = modelMapper.map(eventDTO, EventNode.class);

        setEventCityForUpdate(eventNode, eventDTO);

        eventNode.setId(id);
        eventNode.setTarget(updatedTarget);

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