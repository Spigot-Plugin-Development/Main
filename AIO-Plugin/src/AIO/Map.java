package AIO;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class Map implements Listener {

	@EventHandler
	public void onInitMap(MapInitializeEvent event) {
		
		MapView view = event.getMap();
		
		for (MapRenderer render : view.getRenderers()) {
			view.removeRenderer(render);
		}
		
		view.addRenderer(new MapRenderer() {
			@Override
			public void render(MapView view, MapCanvas canvas, Player player) {
				
				canvas.drawText(10, 15, MinecraftFont.Font, "Welcome to the server!");
				
				//Allow staff to create maps, store them in file with string key, retrieve with command
				
			}
		}); 
		
	}
	
}
