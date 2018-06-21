package AIO;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class Advertisement {
	private Player player;
	private String text;
	private BarColor color;
	private BarStyle style;
	private double time;
	private double maxTime;
	
	Advertisement() {
		this.player = null;
		this.text = "";
		this.color = BarColor.WHITE;
		this.style = BarStyle.SEGMENTED_20;
		this.time = 30d;
		this.maxTime = 30d;
	}
	
	Advertisement(Player player, String text, BarColor color, BarStyle style, double time) {
		this.player = player;
		this.text = text;
		this.color = color;
		this.style = style;
		this.time = time;
		this.maxTime = time;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getText() {
		return text;
	}
	
	public BarColor getColor() {
		return color;
	}
	
	public BarStyle getStyle() {
		return style;
	}
	
	public double getTime() {
		return time;
	}

	public double getMaxTime() {
		return maxTime;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setColor(BarColor color) {
		this.color = color;
	}
	
	public void setStyle(BarStyle style) {
		this.style = style;
	}
	
	public void setTime(double time) {
		this.time = time;
	}
	
	public void setMaxTime(double maxTime) {
		this.maxTime = maxTime;
	}
}
