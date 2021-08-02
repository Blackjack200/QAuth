package com.github.blackjack200;

import cn.hutool.core.io.FileUtil;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.github.blackjack200.listener.DefaultListener;
import lombok.Getter;

public class QAuthLoader extends PluginBase {
	@Getter
	private static QAuthLoader instance;
	@Getter
	private static String kickMessage;

	@Override
	public void onEnable() {
		instance = this;
		this.saveResource("redis.json");
		this.saveResource("config.json");
		Config cfg = new Config(FileUtil.file(this.getDataFolder(), "config.json"), Config.DETECT);
		kickMessage = cfg.getString("kick_message");
		GlobalJedisConfig.set(new JedisConfig(FileUtil.file(this.getDataFolder(), "redis.json")));
		this.getServer().getPluginManager().registerEvents(new DefaultListener(), this);
		this.getLogger().info("QAuth enabled");
	}
}
