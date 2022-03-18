package me.truemb.tvc.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.truemb.tvc.main.Main;

public class TwitchViewerControlCOMMAND implements CommandExecutor, TabCompleter {
	
	private Main instance;
	private List<String> subCommands = new ArrayList<>();
	
	public TwitchViewerControlCOMMAND(Main plugin) {
		this.instance = plugin;
		
		this.subCommands.add("reload");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {

			if (!sender.hasPermission(this.instance.manageFile().getString("Permissions.shuffleTimer"))) {
				sender.sendMessage(this.instance.getMessage("noPermission"));
				return true;
			}
			
			this.instance.disablePlugin();
			this.instance.enablePlugin();
			
			sender.sendMessage(this.instance.getMessage("reloaded"));
			return true;
		}else {
			sender.sendMessage(this.instance.getMessage("help"));
			return true;
		}
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
