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
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;

import me.truemb.tvc.main.Main;
import me.truemb.tvc.twitch.listener.TwitchListener;

public class TwitchChannel {
	
	private String channelName;
	private String ingameName;
	
	private TwitchClientPool twitchClient;
	
	public TwitchChannel(Main plugin, TwitchListener listener, String channelName, String ingameName, String clientId, String clientSecret, String authToken, String userId) {
		this.channelName = channelName;
		this.ingameName = ingameName;
		
		this.twitchClient = this.buildClient(plugin, listener, clientId, clientSecret, authToken, userId);
	}

	private TwitchClientPool buildClient(Main plugin, TwitchListener listener, String clientId, String clientSecret, String authToken, String userId) {

		OAuth2Credential credentials = new OAuth2Credential("twitch", authToken);
		
		TwitchClientPool client = TwitchClientPoolBuilder.builder()
			    .withClientId(clientId)
			    .withClientSecret(clientSecret)
	            .withChatAccount(credentials)
			    .withEnableChat(true)
			    .withEnableHelix(true)
	            .withEnableGraphQL(true)
				.withEnablePubSub(true)
			    .withDefaultEventHandler(ReactorEventHandler.class)
			    .build();

		client.getClientHelper().enableStreamEventListener(this.channelName);
		client.getClientHelper().enableFollowEventListener(this.channelName);
		
		client.getChat().joinChannel(this.channelName);

		
		client.getPubSub().listenForFollowingEvents(credentials, userId);
		client.getPubSub().listenForRaidEvents(credentials, userId);
		client.getPubSub().listenForChannelPointsRedemptionEvents(credentials, userId);
		
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

}
