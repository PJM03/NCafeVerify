package pic_e.ncafeverify;

import org.bukkit.plugin.java.JavaPlugin;

import pic_e.ncafeverify.api.skript.SkriptManager;
import pic_e.ncafeverify.listeners.CommandListener;
import pic_e.ncafeverify.listeners.EventListener;
import pic_e.ncafeverify.managers.ConfigManager;
import pic_e.ncafeverify.managers.VerifyManager;
import pic_e.ncafeverify.util.Util;

public class NCafeVerifyPlugin extends JavaPlugin{
	public static NCafeVerifyPlugin instance;
	public static final String METADATA_KEY = "ncv-privacy-message";
	
	@Override
	public void onEnable() {
		instance = this;
		Util.init();
		ConfigManager.init();
		VerifyManager.load(getDataFolder());
		CommandListener.registerCommand();
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		SkriptManager.register();
	}
	
	@Override
	public void onDisable() {
		VerifyManager.save(getDataFolder());
	}
}
