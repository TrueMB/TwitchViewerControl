package me.truemb.tvc.entities;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.truemb.tvc.utils.Methodes;

public class BukkitEntityInstance extends EntityInstance{
	
	private EntityType type;
	private int amount = 1;
	private String displayName;
	
	private boolean exactLocation;
	
	public BukkitEntityInstance(EntityType type) {
		this(type, 1);
	}
	
	public BukkitEntityInstance(EntityType type, int amount) {
		this(type, amount, null);
	}
	
	public BukkitEntityInstance(EntityType type, int amount, String displayName) {
		this(type, amount, displayName, false);
	}
	
	public BukkitEntityInstance(EntityType type, int amount, String displayName, boolean exactLocation) {
		this.type = type;
		this.amount = amount > 0 ? amount : 1;
		this.displayName = displayName;
		this.exactLocation = exactLocation;
	}
	
	@Override
	public void spawn(Location loc) {
		List<Location> locations = Methodes.getCircle(loc, this.amount / 5 + 3, this.amount);
		
		for(int i = 0; i < this.amount; i++) {
			Location locs = this.exactLocation ? loc : locations.get(i);
			
			Entity en = locs.getWorld().spawnEntity(locs, type);
			if(this.displayName != null) {
				en.setCustomNameVisible(true);
				en.setCustomName(this.displayName);
			}
		}
		
	}

}
