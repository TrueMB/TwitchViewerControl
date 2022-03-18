package me.truemb.tvc.twitch.manager;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.truemb.tvc.utils.Methodes;

public class EntityInstance {
	
	private EntityType type;
	private int amount = 1;
	private String displayName;
	
	public EntityInstance(EntityType type) {
		this(type, 1);
	}
	
	public EntityInstance(EntityType type, int amount) {
		this(type, amount, null);
	}
	
	public EntityInstance(EntityType type, int amount, String displayName) {
		this.type = type;
		this.amount = amount > 0 ? amount : 1;
		this.displayName = displayName;
	}
	
	public void spawn(Location loc) {
		List<Location> locations = Methodes.getCircle(loc, this.amount / 5 + 3, this.amount);
		
		for(Location locs : locations) {
			Entity en = locs.getWorld().spawnEntity(locs, type);
			if(this.displayName != null) {
				en.setCustomNameVisible(true);
				en.setCustomName(this.displayName);
			}
		}
		
	}

}
