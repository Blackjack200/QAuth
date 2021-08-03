package com.github.blackjack200.qauth;

import cn.hutool.core.io.FileUtil;
import cn.nukkit.event.HandlerList;
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
		boolean errorFound = false;
		this.saveResource("redis.json");
		this.saveResource("配置文件.json");
		this.saveResource("LICENSE");
		try {
			Config cfg = new Config(FileUtil.file(this.getDataFolder(), "配置文件.json"), Config.DETECT);
			kickMessage = cfg.getString("踢出消息");
			timeout = cfg.getLong("验证码时效");
			if (timeout <= 0) {
				throw new AssumptionFailedException("配置文件中的验证码时效应为大于0的整数");
			}
		} catch (AssumptionFailedException exception) {
			this.disableWithError(exception);
			errorFound = true;
		}
		try {
			GlobalJedisConfig.set(new JedisConfig(FileUtil.file(this.getDataFolder(), "redis.json")));
		} catch (Throwable throwable) {
			this.disableWithError(throwable);
			errorFound = true;
		}
		if (!errorFound) {
			this.getServer().getPluginManager().registerEvents(new DefaultListener(), this);
			this.getLogger().info("QAuth 插件已经正确加载");
		}
	}

	private void disableWithError(Throwable throwable) {
		this.getServer().getPluginManager().disablePlugin(this);
		this.getLogger().error("QAuth 插件遇到了致命错误", throwable);
		this.getLogger().error("QAuth 插件在开启时遇到了致命错误, 已关闭");
		HandlerList.unregisterAll(this);
	}
}
