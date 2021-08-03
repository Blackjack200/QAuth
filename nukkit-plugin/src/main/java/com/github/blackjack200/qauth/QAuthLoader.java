package com.github.blackjack200.qauth;

import cn.hutool.core.io.FileUtil;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.github.blackjack200.qauth.listener.DefaultListener;
import com.github.blackjack200.qauth.qauth.GlobalJedisConfig;
import com.github.blackjack200.qauth.qauth.JedisConfig;
import com.github.blackjack200.qauth.qauth.utils.AssumptionFailedException;
import lombok.Getter;

public class QAuthLoader extends PluginBase {
	@Getter
	private static QAuthLoader instance;
	@Getter
	private static String kickMessage;
	@Getter
	private static long timeout;

	@Override
	public void onEnable() {
		instance = this;
		this.saveResource("redis.json");
		this.saveResource("config.json");
		this.saveResource("LICENSE");
		Config cfg = new Config(FileUtil.file(this.getDataFolder(), "config.json"), Config.DETECT);
		kickMessage = cfg.getString("kick_message");
		timeout = cfg.getLong("timeout");
		if (timeout <= 0) {
			throw new AssumptionFailedException("config timeout should not be a negative number");
		}
		GlobalJedisConfig.set(new JedisConfig(FileUtil.file(this.getDataFolder(), "redis.json")));
		this.getServer().getPluginManager().registerEvents(new DefaultListener(), this);
		this.getLogger().info("QAuth enabled");
	}
}
