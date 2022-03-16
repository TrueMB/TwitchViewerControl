package me.truemb.tvc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import me.truemb.tvc.main.Main;

public class TwitchViewerControlCOMMAND implements CommandExecutor {
	
	private Main instance;
	
	public TwitchViewerControlCOMMAND(Main plugin) {
		this.instance = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {

			if (!sender.hasPermission(this.instance.manageFile().getString("Permissions.shuffleTimer"))) {
				sender.sendMessage(this.instance.getMessage("noPermission"));
				return true;
			}
			
			
			
			return true;
		}else {
			sender.sendMessage(this.instance.getMessage("help"));
			return true;
		}
	}
}
