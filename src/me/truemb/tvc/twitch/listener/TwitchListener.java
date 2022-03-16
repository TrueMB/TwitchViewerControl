package me.truemb.tvc.twitch.listener;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.domain.ChannelPointsUser;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import com.google.common.collect.Lists;

import me.truemb.tvc.main.Main;
import me.truemb.tvc.twitch.manager.TwitchReward;

public class TwitchListener{
	
	private Main instance;
	
	public TwitchListener(Main plugin) {
		this.instance = plugin;
	}

	public void onLiveStream(ChannelGoLiveEvent e) {

	}

	public void onOfflineStream(ChannelGoOfflineEvent e) {

	}

	public void onFollow(FollowEvent e) {

	}

	public void onRaid(RaidEvent e) {

	}

	public void onSub(SubscriptionEvent e) {

	}

	public void onChat(ChannelMessageEvent e) {

	}
	
	public void onChannelPointsRedemption(ChannelPointsRedemptionEvent e) {
		ChannelPointsRedemption redemption = e.getRedemption();
		ChannelPointsReward reward = redemption.getReward();
		String title = reward.getTitle();
		
		ChannelPointsUser user = redemption.getUser();
		String channelId = redemption.getChannelId();
		
		System.out.println("TODO CHECK: " + channelId);
		
		TwitchReward twitchReward = this.instance.getTwitch().getReward(title);
		
		if(twitchReward == null) {
			this.instance.getLogger().warning("Couldnt find the Reward: '" + title + "' in the Config/Cache.");
			return;
		}
		
		Collection<? extends Player> all = this.getTargetPlayers(title);
		
		//SEND ANNOUNCING IF NEEDED
		if(this.instance.manageFile().getBoolean("Options.doRewardAnnouncing")) {
			all.forEach(p -> p.sendMessage(this.instance.getMessage("rewardAnnouncing")
				.replaceAll("(?i)%" + "username" + "%", user.getDisplayName())
				.replaceAll("(?i)%" + "reward" + "%", title))
			);
		}
	}
	
	private Collection<? extends Player> getTargetPlayers(String channelName){

		boolean onlyStreamer = this.instance.manageFile().getBoolean("Options.OnlyStreamer");
		boolean everyone = this.instance.manageFile().getBoolean("Options.Everyone");
		
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		int onlineSize = onlinePlayers.size();
		
		if(onlineSize <= 0)
			return null;
		
		//EVERYONE IS TARGET, NO SPECIFICATION NEEDED
		if(everyone)
			return Bukkit.getOnlinePlayers();
		
		//ONLY THE STREAMER IS THE TARGET
		if(onlyStreamer)
			for(String channelNames : this.instance.manageFile().getConfigurationSection("Options.Twitch").getKeys(false))
				if(channelNames.equalsIgnoreCase(channelName)) {
					String ingameName = this.instance.manageFile().getString("Options.Twitch.IngameName");
					return Bukkit.getOnlinePlayers().stream().filter(t -> t.getName().equalsIgnoreCase(ingameName)).collect(Collectors.toList());
				}
		
		//RANDOM PLAYER ON THE SERVER
		Random r = new Random();
		Player randomPlayer = (Player) onlinePlayers.toArray()[r.nextInt(onlineSize)];
		return Lists.newArrayList(randomPlayer);
	}

}
