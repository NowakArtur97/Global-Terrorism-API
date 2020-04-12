package com.NowakArtur97.GlobalTerrorismAPI.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "Model responsible for Target validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetDTO implements DTONode {

	@ApiModelProperty(notes = "The target's name")
	@NotBlank(message = "{target.target.notBlank}")
	private String target;
}
