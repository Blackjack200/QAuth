package com.blackjack200.qauth.bot;

import com.github.blackjack200.GlobalJedisConfig;
import com.github.blackjack200.RedisWrapper;
import lombok.Getter;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BotServer {
	private static BotServer server;
	@Getter
	private final Bot bot;
	@Getter
	private final String path;
	private final MiraiLogger logger;
	private final ExecutorService pool;
	private final long group;

	public BotServer(Bot bot, long group, String path) {
		this.bot = bot;
		this.group = group;
		this.path = path;
		server = this;
		this.logger = MiraiLogger.create("Server");
		this.pool = Executors.newFixedThreadPool(8);
	}

	public static BotServer getInstance() {
		return server;
	}

	private ArrayList<String> parseArguments(String cmdLine) {
		StringBuilder sb = new StringBuilder(cmdLine);
		ArrayList<String> args = new ArrayList<>();
		boolean notQuoted = true;
		int start = 0;

		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '\\') {
				sb.deleteCharAt(i);
				continue;
			}

			if (sb.charAt(i) == ' ' && notQuoted) {
				String arg = sb.substring(start, i);
				if (!arg.isEmpty()) {
					args.add(arg);
				}
				start = i + 1;
			} else if (sb.charAt(i) == '"') {
				sb.deleteCharAt(i);
				--i;
				notQuoted = !notQuoted;
			}
		}

		String arg = sb.substring(start);
		if (!arg.isEmpty()) {
			args.add(arg);
		}
		return args;
	}

	public void start() {
		this.bot.getEventChannel().subscribeOnce(BotOnlineEvent.class, (e) -> {
			this.getLogger().info("机器人登陆成功,开始加载脚本");
			this.bot.getConfiguration().noNetworkLog();
			this.bot.getConfiguration().noBotLog();
		});

		this.bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, (final GroupMessageEvent e) -> {
			pool.execute(() -> {
				if (e.getGroup().getId() == this.group) {
					String msg = e.getMessage().contentToString();
					if (msg.startsWith("/")) {
						ArrayList<String> parts = parseArguments(msg.substring(1));
						if ("verify".equals(parts.get(0)) && parts.size() == 3) {
							String name = parts.get(1);
							String code = parts.get(2);
							String actualCode = RedisWrapper.getAuthCodeImpl(name, GlobalJedisConfig.newJedis());
							if (actualCode != null && actualCode.equals(code) &&
									RedisWrapper.bindAccountImpl(name,
											e.getSender().getId(),
											Objects.requireNonNull(GlobalJedisConfig.newJedis())
									)
							) { e.getSubject().sendMessage(String.format("玩家 %s 验证成功", name));
							} else {
								e.getSubject().sendMessage(String.format("玩家 %s 验证失败", name));
							}
						}
					}
				}
			});
		});

		this.bot.getEventChannel().subscribeAlways(BotOfflineEvent.class, (e) -> {
			this.getLogger().info("机器人掉线");
			System.exit(1);
		});

		this.bot.login();
	}

	public MiraiLogger getLogger() {
		return this.logger;
	}
}
