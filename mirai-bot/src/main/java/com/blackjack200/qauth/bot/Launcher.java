package com.blackjack200.qauth.bot;

import com.github.blackjack200.GlobalJedisConfig;
import com.github.blackjack200.JedisConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Launcher {
	public static void main(String[] args) {
		System.setProperty("mirai.no-desktop", "mirai.no-desktop");
		String path = System.getProperty("user.dir");
		File file = new File(path, "config.json");
		if (!file.exists()) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			map.put("account", 110L);
			map.put("password", "123456");
			map.put("protocol", "WATCH");
			map.put("group", 114514L);
			FileUtils.file_put_content(file.getAbsolutePath(), (new Gson()).toJson(map));
		}
		File file2 = new File(path, "redis.json");
		if (!file2.exists()) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			map.put("host", "localhost");
			map.put("port", 6379);
			map.put("password", "qazwsx");
			FileUtils.file_put_content(file2.getAbsolutePath(), (new Gson()).toJson(map));
		}
		GlobalJedisConfig.set(new JedisConfig(file2));
		try {
			Map<String, Object> data = (new Gson()).fromJson(FileUtils.file_get_content(file), new TypeToken<LinkedHashMap<?, ?>>() {
			}.getType());
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
			Bot bot = BotFactory.INSTANCE.newBot(((Double) data.get("account")).longValue(), data.get("password").toString(), configuration);
			BotServer server = new BotServer(bot, ((Double) data.get("group")).longValue(), path);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
