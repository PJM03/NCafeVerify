package pic_e.ncafeverify.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pic_e.ncafeverify.NCafeVerifyPlugin;

public class Util {
	private static Logger logger;
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public static void init() {
		logger = NCafeVerifyPlugin.instance.getLogger();
	}
	
	public static String color(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	public static void sendMessage(Player p, String message, Object... args) {
		p.sendMessage(color(String.format(message, args)));
	}
	
	public static void log(Level level, String message) {
		logger.log(level, message);
	}
	
	public static String dateFormat(Date date) {
		return FORMATTER.format(date);
	}
	
	public static String createCode(int len) {
		StringBuffer temp = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < len; i++) {
			temp.append((rnd.nextInt(10)));
		}
		return temp.toString();
	}
}
