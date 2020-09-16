package com.nowakArtur97.globalTerrorismAPI.configuration;

import com.nowakArtur97.globalTerrorismAPI.feature.country.CountryNode;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.json.JsonValue;

@Configuration
class ModelMapperConfiguration {

	@Bean
	ModelMapper getModelMapper() {

		ModelMapper modelMapper = new ModelMapper();

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

		Converter<JsonValue, CountryNode> myConverter = context -> {

			CountryNode destination = context.getDestination();

			destination.setRegion(destination.getRegion());

			return destination;
		};

		modelMapper.addConverter(myConverter);

		return modelMapper;
	}
}
