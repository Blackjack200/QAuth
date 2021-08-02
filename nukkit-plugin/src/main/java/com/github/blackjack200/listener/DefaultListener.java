package com.github.blackjack200.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import com.github.blackjack200.QAuthLoader;
import com.github.blackjack200.RedisWrapper;
import com.github.blackjack200.utils.Blame;

import java.security.SecureRandom;

public class DefaultListener implements Listener {
	@Blame(blame = "NUKKIT FUCK YOU")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLocallyInitialized(PlayerLocallyInitializedEvent event) {
		final Player player = event.getPlayer();
		final String name = player.getName();
		RedisWrapper.getBindAccount(name).onFulfill((bound) -> {
			if (bound == null) {
				SecureRandom rand = new SecureRandom();
				String code = Integer.toString(rand.nextInt(Integer.MAX_VALUE));
				RedisWrapper.setAuthCode(name, code).onFulfill((unused) -> {
					player.kick(QAuthLoader.getKickMessage().replace("[CODE]", code), false);
				}).start();
			} else {
				player.sendMessage("发现玩家绑定账户: " + bound);
			}
		}).start();
	}
}
