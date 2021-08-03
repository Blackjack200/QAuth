package com.blackjack200.qauth.bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.blackjack200.qauth.qauth.GlobalJedisConfig;
import com.github.blackjack200.qauth.qauth.JedisConfig;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Launcher {
	public static void main(String[] args) {
		System.setProperty("mirai.no-desktop", "mirai.no-desktop");
		String path = System.getProperty("user.dir");
		try {
			File file = prepareConfig(path);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> data = mapper.readValue(file, new TypeReference<Map<String, Object>>() {
			});
			BotConfiguration configuration = new BotConfiguration();
			BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.ANDROID_WATCH;

			switch (data.get("protocol").toString().toUpperCase(Locale.ROOT)) {
				case "PAD":
					protocol = BotConfiguration.MiraiProtocol.ANDROID_PAD;
					break;
				case "PHONE":
					protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE;
					break;
				default:
					break;
			}
			configuration.setProtocol(protocol);
			configuration.fileBasedDeviceInfo("device.json");
			configuration.noBotLog();
			Bot bot = BotFactory.INSTANCE.newBot(((Number) data.get("account")).longValue(), data.get("password").toString(), configuration);
			BotServer server = new BotServer(bot, ((Number) data.get("group")).longValue(), path);
			GlobalJedisConfig.set(new JedisConfig(new File(path, "redis.json")));
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@NotNull
	private static File prepareConfig(String path) throws IOException {
		File file = new File(path, "config.json");
		if (!file.exists()) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			map.put("account", 110L);
			map.put("password", "123456");
			map.put("protocol", "WATCH");
			map.put("group", 114514L);
			ObjectMapper mapper = new ObjectMapper();
			mapper.writerWithDefaultPrettyPrinter().writeValue(file, map);
		}
		File file2 = new File(path, "redis.json");
		if (!file2.exists()) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			map.put("host", "localhost");
			map.put("port", 6379);
			map.put("password", "qazwsx");
			ObjectMapper mapper = new ObjectMapper();
			mapper.writerWithDefaultPrettyPrinter().writeValue(file2, map);
		}

		return file;
	}
}
