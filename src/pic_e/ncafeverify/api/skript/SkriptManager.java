package pic_e.ncafeverify.api.skript;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import pic_e.ncafeverify.event.VerifySuccessEvent;
import pic_e.ncafeverify.util.Util;

public class SkriptManager {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register() {
		if(Bukkit.getPluginManager().getPlugin("Skript") != null) {
			Skript.registerExpression((Class) PlayerVerified.class, (Class) Boolean.class, ExpressionType.SIMPLE, new String[] { "%player% verified" });
			Skript.registerExpression((Class) GetPlayerNaverID.class, (Class) String.class, ExpressionType.SIMPLE, new String[] { "%player%'s naverid" });
			Skript.registerEvent("verify success", (Class) VerifyComplete.class, (Class) VerifySuccessEvent.class, new String[] { "verify success" });
			EventValues.registerEventValue((Class) VerifySuccessEvent.class, (Class) Player.class, (Getter) new Getter<Player, VerifySuccessEvent>() {
				public Player get(final VerifySuccessEvent e) {
					return e.getPlayer();
				}
			}, 0);
			Util.log(Level.INFO, "Skript API 등록 완료");
		}
	}
}
