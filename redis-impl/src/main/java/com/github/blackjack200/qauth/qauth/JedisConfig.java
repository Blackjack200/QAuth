package com.github.blackjack200.qauth.qauth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.blackjack200.qauth.qauth.utils.AssumptionFailedException;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;

public class JedisConfig {
	@Getter
	@NonNull
	private final ConfigBean info;

	public JedisConfig(@NonNull File file) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			this.info = mapper.readValue(file, new TypeReference<ConfigBean>() {
			});
		} catch (IOException err) {
			err.printStackTrace();
			throw new AssumptionFailedException("Jackson ObjectMapper should not error when mapping JedisShardInfo");
		}
	}
}
