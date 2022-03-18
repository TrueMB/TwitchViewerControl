package me.truemb.tvc.twitch.manager;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.reactor.ReactorEventHandler;
import com.github.twitch4j.TwitchClientPool;
import com.github.twitch4j.TwitchClientPoolBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import com.google.common.collect.Lists;

import me.truemb.tvc.main.Main;
import me.truemb.tvc.twitch.listener.TwitchListener;

public class TwitchChannel {
	
	private String channelName;
	private String ingameName;
	private String userId;
	
	private TwitchClientPool twitchClient;
	private String authKey;
	
	public TwitchChannel(Main plugin, TwitchListener listener, String channelName, String ingameName, String clientId, String clientSecret, String authToken) {
		this.channelName = channelName;
		this.ingameName = ingameName;
		this.authKey = authToken;
		
		this.twitchClient = this.buildClient(plugin, listener, clientId, clientSecret);
	}

	private TwitchClientPool buildClient(Main plugin, TwitchListener listener, String clientId, String clientSecret) {

		if(clientId == null || clientId.equals("") || clientSecret == null || clientSecret.equals("") || this.authKey == null || this.authKey.equals("")) {
			plugin.getLogger().warning("Please configurate your Twitch Connection in the config.");
			return null;
		}
		
		OAuth2Credential credentials = new OAuth2Credential("twitch", this.authKey);
		
		TwitchClientPool client = TwitchClientPoolBuilder.builder()
			    .withClientId(clientId)
			    .withClientSecret(clientSecret)
	            .withChatAccount(credentials)
			    .withEnableChat(true)
			    .withEnableHelix(true)
				.withEnablePubSub(true)
			    .withDefaultEventHandler(ReactorEventHandler.class)
			    .build();

		client.getClientHelper().enableStreamEventListener(this.channelName);
		client.getClientHelper().enableFollowEventListener(this.channelName);
		
		//client.getChat().joinChannel(this.channelName);

		UserList list = client.getHelix().getUsers(this.authKey, null, Lists.newArrayList(this.channelName)).execute();
		if(list.getUsers().size() > 0) {
			User user = list.getUsers().get(0);
			this.userId = user.getId();
			
			client.getPubSub().listenForFollowingEvents(credentials, this.userId);
			client.getPubSub().listenForRaidEvents(credentials, this.userId);
			client.getPubSub().listenForChannelPointsRedemptionEvents(credentials, this.userId);
		}else
			plugin.getLogger().warning("Could load UserID of Channel: " + this.channelName);
		
		client.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> listener.onLiveStream(event));
		client.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> listener.onOfflineStream(event));
		client.getEventManager().onEvent(SubscriptionEvent.class, event -> listener.onSub(event));
		
		client.getEventManager().onEvent(ChannelMessageEvent.class, event -> listener.onChat(event));
		
		client.getPubSub().getEventManager().onEvent(FollowEvent.class, event -> listener.onFollow(event));
		client.getPubSub().getEventManager().onEvent(RaidEvent.class, event -> listener.onRaid(event));
		client.getPubSub().getEventManager().onEvent(ChannelPointsRedemptionEvent.class, event -> listener.onChannelPointsRedemption(event));
		
		return client;
	}

	public TwitchClientPool getTwitchClient() {
		return this.twitchClient;
	}

	public String getChannelName() {
		return this.channelName;
	}
	
	public String getIngameName() {
		return this.ingameName;
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	public String getAuthKey() {
		return this.authKey;
	}

}
