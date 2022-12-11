package me.truemb.tvc.entities;

import java.util.List;

import org.bukkit.Location;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.truemb.tvc.utils.Methodes;

public class MythicMobsEntityInstance extends EntityInstance{
	
	private MythicMob mm;
	private int amount = 1;
	
	private boolean exactLocation;
	
	public MythicMobsEntityInstance(MythicMob mm) {
		this(mm, 1);
	}
	
	public MythicMobsEntityInstance(MythicMob mm, int amount) {
		this(mm, amount, false);
	}
	
	public MythicMobsEntityInstance(MythicMob mm, int amount, boolean exactLocation) {
		this.mm = mm;
		this.amount = amount > 0 ? amount : 1;
		this.exactLocation = exactLocation;
	}
	
	@Override
	public void spawn(Location loc) {
		List<Location> locations = Methodes.getCircle(loc, this.amount / 5 + 3, this.amount);
		
		for(int i = 0; i < this.amount; i++) {
			Location locs = this.exactLocation ? loc : locations.get(i);

	       this.mm.spawn(BukkitAdapter.adapt(locs), 1);
		}
		
	}

}
