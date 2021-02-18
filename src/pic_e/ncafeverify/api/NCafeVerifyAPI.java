package pic_e.ncafeverify.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import pic_e.ncafeverify.data.VerifyData;
import pic_e.ncafeverify.managers.VerifyManager;

public class NCafeVerifyAPI {
	public static void setComplete(UUID uuid, String naverId) {
		VerifyData data = new VerifyData(naverId, System.currentTimeMillis());
		VerifyManager.complete(uuid, data);
	}
	
	public static VerifyData getVerifyData(UUID uuid) {
		return VerifyManager.getCompleteData(uuid);
	}
	
	public static boolean isVerified(UUID uuid) {
		return VerifyManager.isCompleted(uuid);
	}
	
	public static boolean isProgressing(Player p) {
		return VerifyManager.isProgressing(p);
	}
}
