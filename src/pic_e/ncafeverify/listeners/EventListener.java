package pic_e.ncafeverify.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pic_e.ncafeverify.NCafeVerifyPlugin;
import pic_e.ncafeverify.managers.VerifyManager;

public class EventListener implements Listener{
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		VerifyManager.stopProgress(e.getPlayer());
		if(e.getPlayer().hasMetadata(NCafeVerifyPlugin.METADATA_KEY)) {
			e.getPlayer().removeMetadata(NCafeVerifyPlugin.METADATA_KEY, NCafeVerifyPlugin.instance);
		}
	}
}
