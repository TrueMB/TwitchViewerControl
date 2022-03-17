package me.truemb.tvc.twitch.listener;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.domain.ChannelPointsUser;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import com.google.common.collect.Lists;

import me.truemb.tvc.main.Main;
import me.truemb.tvc.twitch.manager.TwitchChannel;
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
	
	public void onChat(ChannelMessageEvent e) {

	}

	public void onFollow(FollowEvent e) {

		EventUser user = e.getUser();
		EventChannel channel = e.getChannel();
		String channelName = channel.getName();
		TwitchReward twitchReward = this.instance.getTwitch().getReward(this.instance.manageFile().getString("Events.Follow"));
		
		if(twitchReward == null) {
			if(this.instance.manageFile().getBoolean("Options.EnableDebug"))
				this.instance.getLogger().warning("Couldn't find the Reward for a Follow in the Config/Cache.");
			return;
		}
		
		Collection<? extends Player> all = this.getTargetPlayers(channelName);
		
		//SEND ANNOUNCING IF NEEDED
		if(this.instance.manageFile().getBoolean("Options.Announcing.Follow")) {
			all.forEach(p -> p.sendMessage(this.instance.getMessage("followAnnouncing")
				.replaceAll("(?i)%" + "username" + "%", user.getName())
				.replaceAll("(?i)%" + "channel" + "%", channelName))
			);
		}
		
		//SEND REWARD TO PLAYERS
		Bukkit.getScheduler().runTask(this.instance, new Runnable() {
			
			@Override
			public void run() {
				for(Player p : all)
					twitchReward.send(p);
			}
		});
	}

	public void onRaid(RaidEvent e) {

		EventUser user = e.getRaider();
		EventChannel channel = e.getChannel();
		String channelName = channel.getName();
		
		TwitchReward twitchReward = this.instance.getTwitch().getReward(this.instance.manageFile().getString("Events.Raid"));
		
		if(twitchReward == null) {
			if(this.instance.manageFile().getBoolean("Options.EnableDebug"))
				this.instance.getLogger().warning("Couldn't find the Reward for a Follow in the Config/Cache.");
			return;
		}
		
		Collection<? extends Player> all = this.getTargetPlayers(channelName);
		
		//SEND ANNOUNCING IF NEEDED
		if(this.instance.manageFile().getBoolean("Options.Announcing.Raid")) {
			all.forEach(p -> p.sendMessage(this.instance.getMessage("raidAnnouncing")
				.replaceAll("(?i)%" + "username" + "%", user.getName())
				.replaceAll("(?i)%" + "channel" + "%", channelName))
			);
		}
		
		//SEND REWARD TO PLAYERS
		Bukkit.getScheduler().runTask(this.instance, new Runnable() {
			
			@Override
			public void run() {
				for(Player p : all)
					twitchReward.send(p);
			}
		});
	}

	public void onSub(SubscriptionEvent e) {
		
		EventUser user = e.getUser();
		EventUser giftedBy = e.getGiftedBy();
		EventChannel channel = e.getChannel();
		String channelName = channel.getName();

		TwitchReward twitchReward = this.instance.getTwitch().getReward(this.instance.manageFile().getString("Events." + (e.getGifted() ? "GiftedSubscription" : "Subscription")));
		
		if(twitchReward == null) {
			if(this.instance.manageFile().getBoolean("Options.EnableDebug"))
				this.instance.getLogger().warning("Couldn't find the Reward for a Subscription (Gifted?" + String.valueOf(e.getGifted()) + ") in the Config/Cache.");
			return;
		}
		
		Collection<? extends Player> all = this.getTargetPlayers(channelName);
		
		//SEND ANNOUNCING IF NEEDED
		if(this.instance.manageFile().getBoolean("Options.Announcing.Subscription")) {
			all.forEach(p -> p.sendMessage(this.instance.getMessage((e.getGifted() ? "subscriptionGift" : "subscribed") + "Announcing")
				.replaceAll("(?i)%" + "username" + "%", user.getName())
				.replaceAll("(?i)%" + "channel" + "%", channelName)
				.replaceAll("(?i)%" + "gifter" + "%", giftedBy.getName())
				.replaceAll("(?i)%" + "tier" + "%", e.getSubscriptionPlan())
				.replaceAll("(?i)%" + "month" + "%", String.valueOf(e.getMonths()))
				.replaceAll("(?i)%" + "monthcombo" + "%", String.valueOf(e.getMultiMonthDuration()))
				)
			);
		}
		
		//SEND REWARD TO PLAYERS
		Bukkit.getScheduler().runTask(this.instance, new Runnable() {
			
			@Override
			public void run() {
				for(Player p : all)
					twitchReward.send(p);
			}
		});
	}

	public void onChannelPointsRedemption(ChannelPointsRedemptionEvent e) {
		ChannelPointsRedemption redemption = e.getRedemption();
		ChannelPointsReward reward = redemption.getReward();
		String title = reward.getTitle();
		
		ChannelPointsUser user = redemption.getUser();
		String channelId = reward.getChannelId();
		
		Optional<TwitchChannel> optionalChannel = this.instance.getTwitch().getTwitchChannel(channelId);
		if(optionalChannel.isEmpty()) {
			if(this.instance.manageFile().getBoolean("Options.EnableDebug"))
				this.instance.getLogger().warning("Couldn't find the Reward: '" + title + "' in the Config/Cache.");
			return;
		}
		
		TwitchChannel channel = optionalChannel.get();
		String channelName = channel.getChannelName();
		
		TwitchReward twitchReward = this.instance.getTwitch().getReward(this.instance.manageFile().getString("Events.ChannelPoints." + title));
		
		if(twitchReward == null) {
			if(this.instance.manageFile().getBoolean("Options.EnableDebug"))
				this.instance.getLogger().warning("Couldn't find the Reward: '" + title + "' in the Config/Cache.");
			return;
		}
		
		Collection<? extends Player> all = this.getTargetPlayers(channelName);
		
		//SEND ANNOUNCING IF NEEDED
		if(this.instance.manageFile().getBoolean("Options.Announcing.Reward")) {
			all.forEach(p -> p.sendMessage(this.instance.getMessage("rewardAnnouncing")
				.replaceAll("(?i)%" + "username" + "%", user.getDisplayName())
				.replaceAll("(?i)%" + "channel" + "%", channelName)
				.replaceAll("(?i)%" + "reward" + "%", title))
			);
		}
		
		//SEND REWARD TO PLAYERS
		Bukkit.getScheduler().runTask(this.instance, new Runnable() {
			
			@Override
			public void run() {
				for(Player p : all)
					twitchReward.send(p);
			}
		});
	}
	
	private Collection<? extends Player> getTargetPlayers(String channelName){

		boolean onlyStreamer = this.instance.manageFile().getBoolean("Options.OnlyStreamer");
		boolean everyone = this.instance.manageFile().getBoolean("Options.Everyone");
		
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		int onlineSize = onlinePlayers.size();
		
		if(onlineSize <= 0)
			return Collections.emptyList();
		
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
