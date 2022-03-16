package me.truemb.tvc.twitch.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import me.truemb.tvc.main.Main;
import me.truemb.tvc.twitch.listener.TwitchListener;
import me.truemb.tvc.twitch.manager.TwitchChannel;
import me.truemb.tvc.twitch.manager.TwitchReward;

public class TwitchMain {
	
	private Collection<TwitchChannel> twitchChannels = new ArrayList<TwitchChannel>();
	private HashMap<String, TwitchReward> rewardsHash = new HashMap<String, TwitchReward>();
	
	public TwitchMain(Main plugin) {
		
		//LISTENER - Only register Once since the Methods getting called directly from the channels.
		TwitchListener twitchListener = new TwitchListener(plugin);

		//LOAD AND CACHE REWARDS
		plugin.manageFile().getConfigurationSection("Rewards").getKeys(false).forEach(rewardKey -> {
			this.rewardsHash.put(rewardKey.toLowerCase(), new TwitchReward(plugin, rewardKey));
		});
		
		//LOAD TWITCH CHANNELS
		plugin.manageFile().getConfigurationSection("Options.Twitch").getKeys(false).forEach(channelName -> {
			String ingameName = plugin.manageFile().getString("Options.Twitch." + channelName + ".IngameName");
			String userId = plugin.manageFile().getString("Options.Twitch." + channelName + ".UserId");
			String clientId = plugin.manageFile().getString("Options.Twitch." + channelName + ".ClientId");
			String clientSecret = plugin.manageFile().getString("Options.Twitch." + channelName + ".ClientSecret");
			String oauthToken = plugin.manageFile().getString("Options.Twitch." + channelName + ".OAuth");
			
			this.twitchChannels.add(new TwitchChannel(plugin, twitchListener, channelName, ingameName, clientId, clientSecret, oauthToken, userId));
		});
		
	}
	
	public TwitchReward getReward(String rewardKey) {
		return this.rewardsHash.get(rewardKey);
	}

}
