package com.NowakArtur97.GlobalTerrorismAPI.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(description = "Model responsible for Group validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO implements DTONode {

    @ApiModelProperty(notes = "The group's name", required = true)
    @NotBlank(message = "{group.name.notBlank}")
    private String name;

    @ApiModelProperty(notes = "The event's caused by the group")
    @Valid
    @NotNull(message = "{group.eventsCaused.notNull}")
    private List<EventDTO> eventsCaused;
}
