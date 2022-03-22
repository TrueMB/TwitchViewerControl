package me.truemb.tvc.twitch.manager;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.truemb.tvc.main.Main;
import me.truemb.tvc.utils.UTF8YamlConfiguration;

public class TwitchReward {
	
	private Collection<ItemStack> items = new ArrayList<ItemStack>();
	private Collection<PotionEffect> effects = new ArrayList<PotionEffect>();
	private Collection<EntityInstance> entities = new ArrayList<EntityInstance>();
	
	private boolean healPlayer;
	private boolean feedPlayer;

	public TwitchReward(Main plugin, String rewardKey) {
		UTF8YamlConfiguration config = plugin.manageFile();
		
		config.getConfigurationSection("Rewards." + rewardKey).getKeys(false).forEach(function -> {
			
			if(function.equalsIgnoreCase("heal"))
				this.healPlayer = config.getBoolean("Rewards." + rewardKey + "." + function);
			
			else if(function.equalsIgnoreCase("feed"))
				this.feedPlayer = config.getBoolean("Rewards." + rewardKey + "." + function);
			
			else if(function.equalsIgnoreCase("effects")) {
				config.getConfigurationSection("Rewards." + rewardKey + "." + function).getKeys(false).forEach(effectPath -> {
					
					PotionEffectType type = PotionEffectType.getByName(config.getString("Rewards." + rewardKey + "." + function + "." + effectPath + ".type").toUpperCase());
					int duration = config.getInt("Rewards." + rewardKey + "." + function + "." + effectPath + ".duration") * 20;
					int amplifier = config.getInt("Rewards." + rewardKey + "." + function + "." + effectPath + ".amplifier");
					
					PotionEffect effect = new PotionEffect(type, duration, amplifier);
					this.effects.add(effect);
				});
				
			}else if(function.equalsIgnoreCase("items")) {
				config.getConfigurationSection("Rewards." + rewardKey + "." + function).getKeys(false).forEach(itemPath -> {
					
					Material type = Material.getMaterial(config.getString("Rewards." + rewardKey + "." + function + "." + itemPath + ".type").toUpperCase());
					int amount = config.getInt("Rewards." + rewardKey + "." + function + "." + itemPath + ".amount");
					String displayName = config.isSet("Rewards." + rewardKey + "." + function + "." + itemPath + ".displayName") ? plugin.translateHexColorCodes(config.getString("Rewards." + rewardKey + "." + function + "." + itemPath + ".displayName")) : null;
					
					ItemStack item = new ItemStack(type, amount);
					
					if(displayName != null) {
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(displayName);
						item.setItemMeta(meta);
					}
					
					this.items.add(item);
				});
				
			}else if(function.equalsIgnoreCase("spawnEntities")) {
				config.getConfigurationSection("Rewards." + rewardKey + "." + function).getKeys(false).forEach(entityPath -> {
					
					EntityType type = EntityType.valueOf(config.getString("Rewards." + rewardKey + "." + function + "." + entityPath + ".type").toUpperCase());
					int amount = config.getInt("Rewards." + rewardKey + "." + function + "." + entityPath + ".amount");
					String displayName = config.isSet("Rewards." + rewardKey + "." + function + "." + entityPath + ".displayName") ? plugin.translateHexColorCodes(config.getString("Rewards." + rewardKey + "." + function + "." + entityPath + ".displayName")) : null;
					boolean exactLocation = config.getBoolean("Rewards." + rewardKey + "." + function + "." + entityPath + ".exactLocation");
					
					this.entities.add(new EntityInstance(type, amount, displayName, exactLocation));
				});
				
			}else
				plugin.getLogger().warning("I am not sure what to do with following Reward Option: '" + "Rewards." + rewardKey + "." + function + "'. Is it a valid Value?");
			
		});
		
	}
	
	/**
	 * Sends the Cached Reward to the Target Player
	 * 
	 * @param Target Player
	 */
	public void send(Player p) {
		
		//HEAL PLAYER
		if(this.healPlayer)
			p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		
		//FEED PLAYER
		if(this.feedPlayer)
			p.setFoodLevel(20);
		
		if(this.items.size() > 0)
			this.items.forEach(item -> p.getInventory().addItem(item));
		
		if(this.effects.size() > 0) {
			this.effects.forEach(potionEffect -> {
				PotionEffect current = p.getPotionEffect(potionEffect.getType());
				if(current == null || current.getAmplifier() < potionEffect.getAmplifier())
					p.addPotionEffect(potionEffect);
				else {
					p.addPotionEffect(new PotionEffect(potionEffect.getType(), potionEffect.getDuration() + current.getDuration(), potionEffect.getAmplifier() > current.getAmplifier() ? potionEffect.getAmplifier() : current.getAmplifier()));
				}
				
			}); 
		}

		if(this.entities.size() > 0)
			this.entities.forEach(entityInstance -> entityInstance.spawn(p.getLocation()));
				
		
	}
}
