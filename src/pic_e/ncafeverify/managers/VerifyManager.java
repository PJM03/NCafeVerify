package pic_e.ncafeverify.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pic_e.ncafeverify.NCafeVerifyPlugin;
import pic_e.ncafeverify.data.VerifyData;
import pic_e.ncafeverify.util.Util;

public class VerifyManager {
	private static HashMap<UUID, VerifyData> completes = new HashMap<>();
	private static HashMap<UUID, String[]> progress = new HashMap<>();
	
	public static void startProgress(Player p, String naverId) {
		String code = Util.createCode(ConfigManager.getCodeLength());
		progress.put(p.getUniqueId(), new String[] {code, naverId});
		ConfigManager.getConfigMessage("start").sendMessageWithReplace(p, new String[] {"%title%", ConfigManager.getTitlePrefix() + code});
	}
	
	public static void complete(Player p) {
		VerifyData data = new VerifyData(progress.get(p.getUniqueId())[1], System.currentTimeMillis());
		completes.put(p.getUniqueId(), data);
	}
	
	public static void complete(UUID uuid, VerifyData data) {
		completes.put(uuid, data);
	}
	
	public static void stopProgress(Player p) {
		if(p.hasMetadata(NCafeVerifyPlugin.METADATA_KEY)) {
			p.removeMetadata(NCafeVerifyPlugin.METADATA_KEY, NCafeVerifyPlugin.instance);
		}
		if(isProgressing(p)) progress.remove(p.getUniqueId());
	}
	
	public static String getCode(Player p) {
		return progress.get(p.getUniqueId())[0];
	}
	
	public static VerifyData getCompleteData(UUID uuid) {
		return completes.get(uuid);
	}
	
	public static void removeCompleteData(UUID uuid) {
		completes.remove(uuid);
	}
	
	public static boolean isProgressing(Player p) {
		return progress.containsKey(p.getUniqueId());
	}
	
	public static boolean isCompleted(UUID uuid) {
		return completes.containsKey(uuid);
	}
	
	public static boolean confirm(Player p) {
		Document d;
		Elements esd;
		Element ed = null;
		try {
			d = Jsoup.connect(ConfigManager.getCafeURL()).get();
			String data = d.select("script").toString();
			String clubId= data.split("g_sClubId = \"")[1].split("\";")[0];
			d = Jsoup.connect("https://cafe.naver.com/ArticleList.nhn?search.clubid=" + clubId).get();
			esd = d.select("tr>td.td_article>div.board-list>div.inner_list>a");
			for(Element e : esd) {
				String t = e.text();
				if(t.contains(ConfigManager.getTitlePrefix())) {
					if(t.replace(ConfigManager.getTitlePrefix(), "").equals(progress.get(p.getUniqueId())[0])) {
						ed = e;
						break;
					}
				}
//				if(t.contains(":")) {
//					if(t.split(":")[1].equals(progress.get(p.getUniqueId())[0])) {
//						ed = e;
//						break;
//					}
//				}
			}
			if(ed == null) return false;
			data = ed.parent().parent().parent().parent().select("td.td_name>div.pers_nick_area>table>tbody>tr>td.p-nick>a").attr("onclick");
			String naverId = data.split("ui\\(event, '")[1].split("',")[0];
			return naverId.equals(progress.get(p.getUniqueId())[1]);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void load(File dataFolder) {
		ObjectInputStream ois = null;
		File dataFile = new File(dataFolder, "verify.dat");
		try {
			if(dataFile.exists()) {
				ois = new ObjectInputStream(new FileInputStream(dataFile));
				completes = (HashMap<UUID, VerifyData>) ois.readObject();
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ois != null) ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void save(File dataFolder) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(dataFolder, "verify.dat")));
			oos.writeObject(completes);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(oos != null) oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
