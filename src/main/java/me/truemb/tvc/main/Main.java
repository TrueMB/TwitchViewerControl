package me.truemb.tvc.main;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;

import me.truemb.tvc.commands.TwitchViewerControlCOMMAND;
import me.truemb.tvc.twitch.main.TwitchMain;
import me.truemb.tvc.utils.ConfigUpdater;
import me.truemb.tvc.utils.UTF8YamlConfiguration;

public class Main extends JavaPlugin {
		
	private UTF8YamlConfiguration config;
	private TwitchMain twitchMainInstance;
	
	private static final int configVersion = 4;
    private static final String SPIGOT_RESOURCE_ID = "100882";
    private static final int BSTATS_PLUGIN_ID = 14642;
    
	public void onEnable() {
		
		this.enablePlugin();
		
		//COMMANDS
		this.getCommand("twitchviewercontrol").setExecutor(new TwitchViewerControlCOMMAND(this));
		
		//METRICS ANALYTICS
		if(this.manageFile().getBoolean("Options.useMetrics"))
			new Metrics(this, BSTATS_PLUGIN_ID);
				
		//UPDATE CHECKER
		this.checkForUpdate();
	}
	
	@Override
	public void onDisable() {
		this.disablePlugin();
	}

	public void enablePlugin() {
		this.manageFile();
		this.twitchMainInstance = new TwitchMain(this);
	}
	
	public void disablePlugin() {
		this.config = null;
		
		if(this.twitchMainInstance != null)
			this.twitchMainInstance.disableClients();
	}

	//CONFIG
	public String getMessage(String path) {
		String s = this.manageFile().getString("Messages.prefix") + " " + this.manageFile().getString("Messages." + path);
		return ChatColor.translateAlternateColorCodes('&', this.translateHexColorCodes(s));
	}

	public String translateHexColorCodes(String message){
		
        final Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = hexPattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
	
	public UTF8YamlConfiguration manageFile() {
		File configFile = this.getConfigFile();
		if (!configFile.exists())
			saveResource("config.yml", true);
		
		if(this.config == null) {
			
			//TO GET THE CONFIG VERSION
			this.config = new UTF8YamlConfiguration(configFile);
			
			//UPDATE
			if(!this.config.isSet("ConfigVersion") || this.config.getInt("ConfigVersion") < configVersion) {
				this.getLogger().info("Updating Config!");
				try {
					ConfigUpdater.update(this, "config.yml", configFile, Lists.newArrayList("Rewards", "Events", "Options.Twitch"));
					this.reloadConfig();
					this.config = new UTF8YamlConfiguration(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return this.config;
	}

	public File getConfigFile() {
		return new File(this.getDataFolder().getPath(), "config.yml");
	}
	
	//CHECK FOR UPDATE
	//https://www.spigotmc.org/threads/powerful-update-checker-with-only-one-line-of-code.500010/
	private void checkForUpdate() {
		
		new UpdateChecker(this, UpdateCheckSource.SPIGET, SPIGOT_RESOURCE_ID)
                .setDownloadLink(SPIGOT_RESOURCE_ID) // You can either use a custom URL or the Spigot Resource ID
                .setDonationLink("https://www.paypal.me/truemb")
                .setChangelogLink(SPIGOT_RESOURCE_ID) // Same as for the Download link: URL or Spigot Resource ID
                .setNotifyOpsOnJoin(true) // Notify OPs on Join when a new version is found (default)
                .setNotifyByPermissionOnJoin(this.getDescription().getName() + ".updatechecker") // Also notify people on join with this permission
                .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                .checkEveryXHours(12) // Check every hours
                .checkNow(); // And check right now
        
	}

	public TwitchMain getTwitch() {
		return this.twitchMainInstance;
	}
}
