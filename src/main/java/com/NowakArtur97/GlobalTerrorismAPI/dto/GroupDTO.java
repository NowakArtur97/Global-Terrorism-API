package com.NowakArtur97.GlobalTerrorismAPI.dto;

import com.NowakArtur97.GlobalTerrorismAPI.baseModel.Group;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@ApiModel(description = "Model responsible for Group validator")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO implements DTONode, Group {

    @ApiModelProperty(notes = "The group's name", required = true, example = "Group")
    @NotBlank(message = "{group.name.notBlank}")
    private String name;

    @ApiModelProperty(notes = "The event's caused by the group", required = true)
    @Valid
    @NotEmpty(message = "{group.eventsCaused.notEmpty}")
    private List<EventDTO> eventsCaused;
}
