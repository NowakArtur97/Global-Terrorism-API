package com.NowakArtur97.GlobalTerrorismAPI.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetDTO {

	@NotBlank(message = "{target.target.notBlank}")
	private String target;
}
