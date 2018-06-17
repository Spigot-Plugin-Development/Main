package AIO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Advertisements {

	private Plugin plugin;
	
	BossBar bossBar;
	private String[] ads = {"test ad", "test ad 2"};
	private BarColor[] adColors = {BarColor.RED, BarColor.PURPLE};
	int serverAdid = 0;
	List<String> playerAds = new ArrayList<String>();
	List<String> players = new ArrayList<String>();
	
	Advertisements(Plugin plugin) {
		this.plugin = plugin;
		
		bossBar = Bukkit.createBossBar("Loading", BarColor.BLUE, BarStyle.SOLID);
		bossBar.setVisible(true);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				bossBar.removeAll();
				
				if (playerAds.isEmpty()) {
					serverAdid = (serverAdid + 1) % ads.length;
					bossBar.setTitle(aio.colorize(ads[serverAdid]));
					bossBar.setColor(adColors[serverAdid]);
					bossBar.setStyle(BarStyle.SOLID);
				} else {
					bossBar.setColor(BarColor.WHITE);
					bossBar.setTitle(aio.colorize(playerAds.get(0)));
					playerAds.remove(0);
					players.remove(0);
					bossBar.setStyle(BarStyle.SEGMENTED_20);
				}
				bossBar.setProgress(1.0);
				for (Player player: Bukkit.getServer().getOnlinePlayers()) {
					bossBar.addPlayer(player);
				}
			}
		}.runTaskTimer(plugin, 20, 20 * 60);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				bossBar.setProgress(bossBar.getProgress() - 1.0 / 240.0);
				
			}
		}.runTaskTimer(plugin, 20, 5);
	}
	
	public void addAd(CommandSender player, String ad) {
		if (players.contains(player.getName())) {
			player.sendMessage("You already have an ad in the queue");
		} else {
			playerAds.add(ad);
			players.add(player.getName());
		}
	}
	
	public void removeBar() {
		bossBar.removeAll();
	}
}
