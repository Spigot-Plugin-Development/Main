package AIO;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class Advertisement {
	private String player;
	private String text;
	private BarColor color;
	private BarStyle style;
	private double time;
	private double maxTime;
	
	Advertisement() {
		this.player = "";
		this.text = "";
		this.color = BarColor.WHITE;
		this.style = BarStyle.SEGMENTED_20;
		this.time = 30d;
		this.maxTime = 30d;
	}
	
	Advertisement(String player, String text, BarColor color, BarStyle style, double time, double maxTime) {
		this.player = player;
		this.text = text;
		this.color = color;
		this.style = style;
		this.time = time;
		this.maxTime = maxTime;
	}
	
	public String getPlayer() {
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
	
	public void setPlayer(String player) {
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
