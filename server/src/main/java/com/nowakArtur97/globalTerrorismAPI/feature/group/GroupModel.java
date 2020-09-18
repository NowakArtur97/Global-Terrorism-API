package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.feature.event.EventModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.Objects;

@ApiModel(description = "Details about the Target")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupModel extends RepresentationModel<GroupModel> implements Group {

    @ApiModelProperty(notes = "The unique id of the Group")
    private Long id;

    @ApiModelProperty(notes = "The group's name")
    private String name;

    @ApiModelProperty(notes = "The event's caused by the group")
    private List<EventModel> eventsCaused;

    public void addEvent(EventModel eventModel) {

        eventsCaused.add(eventModel);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof GroupModel)) return false;

        GroupModel that = (GroupModel) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getName());
    }
}
