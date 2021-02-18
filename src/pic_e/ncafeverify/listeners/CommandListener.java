package pic_e.ncafeverify.listeners;

import java.util.Date;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import pic_e.ncafeverify.NCafeVerifyPlugin;
import pic_e.ncafeverify.data.VerifyData;
import pic_e.ncafeverify.event.VerifySuccessEvent;
import pic_e.ncafeverify.managers.ConfigManager;
import pic_e.ncafeverify.managers.VerifyManager;
import pic_e.ncafeverify.util.Util;

public class CommandListener{
	public static void registerCommand() {
		NCafeVerifyPlugin.instance.getCommand("카페인증").setExecutor(new UserCommand());
		NCafeVerifyPlugin.instance.getCommand("인증관리").setExecutor(new AdminCommand());
	}
	
	private static class UserCommand implements CommandExecutor{
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(p.hasPermission(ConfigManager.getUserPermission())) {
					if(args.length > 0) {
						if(VerifyManager.isCompleted(p.getUniqueId())) {
							ConfigManager.getConfigMessage("already-complete").sendMessage(p);
						}else if(VerifyManager.isProgressing(p)) {
							ConfigManager.getConfigMessage("already-process").sendMessageWithReplace(p, 
									new String[] {"%title%", ConfigManager.getTitlePrefix() + VerifyManager.getCode(p)});
						}else {
							if(p.hasMetadata(NCafeVerifyPlugin.METADATA_KEY)) {
								String id = args[0];
								if(id.length() >= 5 && id.length() <= 20 && id.matches("[0-9a-z-_]+")) {
									VerifyManager.startProgress(p, id);
								}else ConfigManager.getConfigMessage("invalid-id").sendMessage(p);
							}else {
								ConfigManager.getConfigMessage("privacy-message").sendMessage(p);
								p.setMetadata(NCafeVerifyPlugin.METADATA_KEY, new FixedMetadataValue(NCafeVerifyPlugin.instance, true));
							}
						}
					}else {
						if(VerifyManager.isCompleted(p.getUniqueId())) {
							ConfigManager.getConfigMessage("already-complete").sendMessage(p);
						}else if(VerifyManager.isProgressing(p)) {
							ConfigManager.getConfigMessage("checking").sendMessage(p);
							Bukkit.getScheduler().runTaskAsynchronously(NCafeVerifyPlugin.instance, ()->{
								boolean status = VerifyManager.confirm(p);
								if(status) {
									VerifyManager.complete(p);
									VerifySuccessEvent event = new VerifySuccessEvent(p);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										VerifyManager.stopProgress(p);
										ConfigManager.getConfigMessage("success").sendMessage(p);
									}else {
										VerifyManager.removeCompleteData(p.getUniqueId());
									}
								}else {
									ConfigManager.getConfigMessage("fail").sendMessage(p);
								}
							});
						}else {
							ConfigManager.getConfigMessage("help").sendMessage(p);
						}
					}
				}else {
					ConfigManager.getConfigMessage("permission").sendMessage(p);
				}
			}else Util.log(Level.WARNING, "콘솔에서는 사용할 수 없는 명령어입니다.");
			return false;
		}
	}
	private static class AdminCommand implements CommandExecutor{
		@SuppressWarnings("deprecation")
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(p.hasPermission(ConfigManager.getAdminPermission())) {
					if(args.length > 0) {
						if(args[0].equals("확인")) {
							if(args.length == 2) {
								Util.sendMessage(p, "&c정보를 불러오는 중입니다...");
								Bukkit.getScheduler().runTaskAsynchronously(NCafeVerifyPlugin.instance, ()->{
									OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
									if(offlinePlayer == null || (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline())) Util.sendMessage(p, "&c해당 플레이어의 접속기록이 없습니다.");
//									else if(!offlinePlayer.hasPlayedBefore()) Util.sendMessage(p, "&c해당 플레이어의 접속기록이 없습니다.");
									else if(!VerifyManager.isCompleted(offlinePlayer.getUniqueId())) Util.sendMessage(p, "&c해당 플레이어는 인증을 완료하지 않았습니다.");
									else {
										VerifyData data = VerifyManager.getCompleteData(offlinePlayer.getUniqueId());
										Util.sendMessage(p, "&c%s(%s)의 인증 정보", offlinePlayer.getName(), offlinePlayer.getUniqueId().toString());
										Util.sendMessage(p, "&c인증일시: %s", Util.dateFormat(new Date(data.getTime())));
										Util.sendMessage(p, "&c네이버 ID: %s", data.getNaverId());
									}
								});
							}else Util.sendMessage(p, "&c/인증관리 확인 <닉네임>");
						}else if(args[0].equals("리로드")) {
							ConfigManager.reload();
							Util.sendMessage(p, "&c완료되었습니다.");
						}else if(args[0].equals("삭제")) {
							if(args.length == 2) {
								Util.sendMessage(p, "&c정보를 확인 중입니다...");
								Bukkit.getScheduler().runTaskAsynchronously(NCafeVerifyPlugin.instance, ()->{
									OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
									if(offlinePlayer == null || (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline())) Util.sendMessage(p, "&c해당 플레이어의 접속기록이 없습니다.");
//									else if(!offlinePlayer.hasPlayedBefore()) Util.sendMessage(p, "&c해당 플레이어의 접속기록이 없습니다.");
									else if(!VerifyManager.isCompleted(offlinePlayer.getUniqueId())) Util.sendMessage(p, "&c해당 플레이어는 인증을 완료하지 않았습니다.");
									else {
										VerifyManager.removeCompleteData(offlinePlayer.getUniqueId());
										Util.sendMessage(p, "&c삭제되었습니다.");
									}
								});
							}else Util.sendMessage(p, "&c/인증관리 삭제 <닉네임>");
						}else {
							Util.sendMessage(p, "&c/인증관리 확인 <닉네임> - <닉네임>에 해당하는 플레이어의 인증 기록을 확인합니다.");
							Util.sendMessage(p, "&c/인증관리 삭제 <닉네임> - <닉네임>에 해당하는 플레이어의 인증 기록을 삭제합니다.");
							Util.sendMessage(p, "&c/인증관리 리로드 - config.yml을 다시 불러옵니다.");
						}
					}else {
						Util.sendMessage(p, "&c/인증관리 확인 <닉네임> - <닉네임>에 해당하는 플레이어의 인증 기록을 확인합니다.");
						Util.sendMessage(p, "&c/인증관리 삭제 <닉네임> - <닉네임>에 해당하는 플레이어의 인증 기록을 삭제합니다.");
						Util.sendMessage(p, "&c/인증관리 리로드 - config.yml을 다시 불러옵니다.");
					}
				}else {
					ConfigManager.getConfigMessage("permission").sendMessage(p);
				}
			}else Util.log(Level.WARNING, "콘솔에서는 사용할 수 없는 명령어입니다.");
			return false;
		}
	}
}
