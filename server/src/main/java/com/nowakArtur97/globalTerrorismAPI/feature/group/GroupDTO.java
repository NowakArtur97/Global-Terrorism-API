package com.nowakArtur97.globalTerrorismAPI.feature.group;

import com.nowakArtur97.globalTerrorismAPI.common.baseModel.DTO;
import com.nowakArtur97.globalTerrorismAPI.feature.event.EventDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@ApiModel(description = "Model responsible for Group validation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO implements DTO, Group {

    @ApiModelProperty(notes = "The group's name", required = true, example = "Group")
    @NotBlank(message = "{group.name.notBlank}")
    private String name;

    @ApiModelProperty(notes = "The event's caused by the group", required = true)
    @Valid
    @NotEmpty(message = "{group.eventsCaused.notEmpty}")
    private List<EventDTO> eventsCaused;
}
