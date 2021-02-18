package pic_e.ncafeverify.managers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pic_e.ncafeverify.NCafeVerifyPlugin;
import pic_e.ncafeverify.managers.ConfigManager.ConfigMessage.ConfigMessageType;
import pic_e.ncafeverify.util.Util;

public class ConfigManager {
	private static FileConfiguration config;
	
	public static void init() {
		NCafeVerifyPlugin.instance.saveDefaultConfig();
		config = NCafeVerifyPlugin.instance.getConfig();
	}
	
	public static void reload() {
		NCafeVerifyPlugin.instance.reloadConfig();
		config = NCafeVerifyPlugin.instance.getConfig();
	}
	
	public static String getUserPermission() {
		return config.getString("user-permission");
	}
	
	public static String getAdminPermission() {
		return config.getString("admin-permission");
	}
	
	public static int getCodeLength() {
		return config.getInt("code-length");
	}
	
	public static String getCafeURL() {
		return config.getString("cafe-url");
	}
	
	public static String getTitlePrefix() {
		return config.getString("title-prefix");
	}

	public static ConfigMessage getConfigMessage(String id) {
		return new ConfigMessage(ConfigMessageType.valueOf(config.getString("message." + id + ".type")), config.getStringList("message." + id + ".message"));
	}
	
	public static class ConfigMessage {
		private ConfigMessageType type;
		private List<String> message;
		
		public ConfigMessage(ConfigMessageType type, List<String> message) {
			this.type = type;
			this.message = message;
		}
		
		public void sendMessage(Player p) {
			this.type.send(p, this.message);
		}
		
		public void sendMessageWithReplace(Player p, String[]... replacements) {
			this.type.sendWithReplace(p, this.message, replacements);
		}
		
		public static enum ConfigMessageType {
			BROADCAST {
				@Override
				public void send(Player p, List<String> message) {
					message.forEach(msg -> Bukkit.broadcastMessage(Util.color(msg)));
				}
				
				@Override
				public void sendWithReplace(Player p, List<String> message, String[]... replacements) {
					message.forEach(msg -> {
						for(String[] replacement : replacements) {
							msg = msg.replaceAll(replacement[0], replacement[1]);
						}
						Bukkit.broadcastMessage(Util.color(msg));
					});
				}
			},
			ACTION_BAR {
				@Override
				public void send(Player p, List<String> message) {
					p.sendActionBar(Util.color(message.get(0)));
				}
				
				@Override
				public void sendWithReplace(Player p, List<String> message, String[]... replacements) {
					String msg = message.get(0);
					for(String[] replacement : replacements) {
						msg = msg.replaceAll(replacement[0], replacement[1]);
					}
					p.sendActionBar(Util.color(msg));
				}
			},
			TITLE {
				@Override
				public void send(Player p, List<String> message) {
					p.sendTitle(Util.color(message.get(0)), Util.color(message.get(1)), 10, 40, 20);
				}
				
				@Override
				public void sendWithReplace(Player p, List<String> message, String[]... replacements) {
					String msg1 = message.get(0);
					String msg2 = message.get(1);
					for(String[] replacement : replacements) {
						msg1 = msg1.replaceAll(replacement[0], replacement[1]);
						msg2 = msg2.replaceAll(replacement[0], replacement[1]);
					}
					p.sendTitle(Util.color(msg1), Util.color(msg2), 10, 40, 20);
				}
			},
			CHAT {
				@Override
				public void send(Player p, List<String> message) {
					message.forEach(msg -> Util.sendMessage(p, msg));
				}
				
				@Override
				public void sendWithReplace(Player p, List<String> message, String[]... replacements) {
					message.forEach(msg -> {
						for(String[] replacement : replacements) {
							msg = msg.replaceAll(replacement[0], replacement[1]);
						}
						Util.sendMessage(p, msg);
					});
				}
			};
			
			public abstract void send(Player p, List<String> message);
			public abstract void sendWithReplace(Player p, List<String> message, String[]... replacements);
		}
	}
}
