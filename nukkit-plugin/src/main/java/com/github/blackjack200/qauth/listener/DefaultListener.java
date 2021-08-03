package com.github.blackjack200.qauth.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import com.github.blackjack200.qauth.QAuthLoader;
import com.github.blackjack200.qauth.RedisWrapper;
import com.github.blackjack200.qauth.utils.Blame;

import java.security.SecureRandom;
import java.util.function.Consumer;

public class DefaultListener implements Listener {
	@Blame(blame = "NUKKIT FUCK YOU")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLocallyInitialized(PlayerLocallyInitializedEvent event) {
		final Player player = event.getPlayer();
		final String name = player.getName();
		RedisWrapper.getBindAccount(name).onFulfill((account) -> {
			if (account != null) {
				player.sendMessage("发现玩家绑定账户: " + account);
			} else {
				RedisWrapper.getAuthCode(name).onFulfill((code) -> {
					boolean set = code == null;
					if (set) {
						SecureRandom rand = new SecureRandom();
						code = Integer.toString(rand.nextInt(Integer.MAX_VALUE));
					}
					String finalCode = code;
					Consumer<Void> success = (val) -> {
						player.kick(QAuthLoader.getKickMessage().replace("[CODE]", finalCode).replace("[NAME]", name), false);
					};
					if (set) {
						RedisWrapper.setAuthCode(name, code, QAuthLoader.getTimeout()).onFulfill((unused) -> {
							success.accept(null);
						}).start();
					} else {
						success.accept(null);
					}
				}).start();
			}
		}).start();
	}

	private void handle(Player player, String code) {

	}
}
