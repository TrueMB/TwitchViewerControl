package me.truemb.tvc.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.github.twitch4j.helix.domain.CustomReward;

import me.truemb.tvc.main.Main;
import me.truemb.tvc.twitch.manager.TwitchChannel;

public class TwitchViewerControlCOMMAND implements CommandExecutor, TabCompleter {
	
	private Main instance;
	private List<String> subCommands = new ArrayList<>();
	
	public TwitchViewerControlCOMMAND(Main plugin) {
		this.instance = plugin;
		
		this.subCommands.add("reload");
		this.subCommands.add("setupRewards");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {

			if (!sender.hasPermission(this.instance.manageFile().getString("Permissions.adminCommand"))) {
				sender.sendMessage(this.instance.getMessage("noPermission"));
				return true;
			}
			
			this.instance.disablePlugin();
			this.instance.enablePlugin();
			
			sender.sendMessage(this.instance.getMessage("reloaded"));
			return true;
			
		}else if(args.length == 2 && args[0].equalsIgnoreCase("setupRewards")) {

			String channelName = args[1];
			String ingameNameStreamer = this.instance.manageFile().getString("Options.Twitch." + channelName + ".IngameName");
			
			if(ingameNameStreamer == null) {
				sender.sendMessage(this.instance.getMessage("configChannelMissing"));
				return true;
			}
			
			if (!sender.hasPermission(this.instance.manageFile().getString("Permissions.adminCommand")) && !sender.getName().equalsIgnoreCase("ingameNameStreamer")) {
				sender.sendMessage(this.instance.getMessage("noPermission"));
				return true;
			}
			
			boolean success = this.setupChannelRewards(channelName);
			
			sender.sendMessage(this.instance.getMessage(success ? "channelRewardsCreated" : "couldntCreateChannelRewards"));
			return true;
		}else {
			sender.sendMessage(this.instance.getMessage("help"));
			return true;
		}
	}
	
	private boolean setupChannelRewards(String channelName) {
		Optional<TwitchChannel> optionalChannel = this.instance.getTwitch().getTwitchChannelByName(channelName);
		if(optionalChannel.isEmpty())
			return false;
		
		TwitchChannel channel = optionalChannel.get();
		
		this.instance.manageFile().getConfigurationSection("Events.ChannelRewards").getKeys(false).forEach(title -> {
			
			CustomReward reward = CustomReward.builder()
					.title(title)
					.cost(this.instance.manageFile().getInt("Events.ChannelRewards." + title + ".ChannelPoints"))
					.build();
			
			channel.getTwitchClient().getHelix().createCustomReward(channel.getAuthKey(), channel.getUserId(), reward).queue();
		});
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		List<String> result = new ArrayList<>();

		if(args.length == 1)
			for(String subCMD : this.subCommands)
				if(subCMD.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(subCMD);
		return result;
	}
}
