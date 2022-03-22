# TwitchViewerControl
You are a Streamer and want more interaction with your Chat in your Minecraft?
Then you should take a look into TwitchViewerControl (TVC).

As a smaller Streamer on my own I know that interactions with the Chat is important to keep the Viewers.
And also I often hear the question, **what can I do with the Channel Points?**
So I came up with this Solution.

And yes, there are Mods and Programs that are doing the similar, but none is for Bukkit Multiplayer (atm).
As a plugin it gives the functionality to support any server, without doing anything on the Client Side.
So perfect, if you want to have a fun Stream with friends on your server!

Depending on how you set up the config, it could be useful f.e. for Jump and Run Maps, Survival (or Hardcore) and PvP.
Everything is customizable!

# More Informations about the plugin
In the config you can find a Section that is named **Events**.
This are Twitch Events that the Viewer could trigger.
They contain always a Reward group, which is going to be triggert.

![Config Events](https://i.ibb.co/68ghfgs/Bild-2022-03-18-082358.png)

The **Reward** groups contains all the Stuff, that is going to be happening ingame.
Currently there are following Minecraft Events:
- spawnEntities - Can contain multiple Entities, which will be spawned.
  - type: ![Entity Type](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html)
  - amount: how many should be spawned
  - exactLocation: Should the Entity spawn on the Player Position.
- items - Can contain multiple Items, which the Player will gain.
  - type: ![Material Type](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)
  - amount: How many items should be added
  - displayName: A Custom Name
- effects - Can contain multiple PotionEffects for a Player. Stackable, if multiple times triggert.
  - type: ![Potion Type](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html)
  - duration: The Duration of the effect
  - amplifier: How strong the effect should be
- heal - Heals the player
- feed - Feeds the player

![Config Rewards](https://i.ibb.co/cTsPFPT/Bild-2022-03-18-082308.png)
![Fast Trader](https://i.ibb.co/7rzgRm1/Fast-Trader.png)
![Creeper](https://i.ibb.co/dBtkn26/Creeper.png)
![Effect](https://i.ibb.co/QnbF5qc/Effect.png)

# Commands:
- /TVC reload - Reloads the Config and the Twitch Connections
- /TVC setupRewards <Twitch Channel> - Creates the ChannelPoints with the config given Coins Amount without accepts through the Mod/Streamer. (They still can be edited) An Admin or the Ingamename in the Config can use the command.

# Things you should know:
- Plugin needs to be running, to read the Twitch Events.
- Either my plugin or the Streamer (you) can give back Channel Points, if something went wrong.
- This plugin should not run on public online-mode false servers for your own safety.

# How to setup:
As the first thing, we need to create an app on Twitch.

https://dev.twitch.tv/console/apps/create

- Name can be anything, but dont use 'Twitch' in the name, since it wont work then
- Redirect URL can be "https://localhost" if you dont know, what to do there.
- Category is a Game Integration.

After you have done the Captcha and clicked on save, you need to go back in the settings with **manage**.
There will be now a Client-ID.
You will also need the Client-Secret. Press on **new Secret** to generate one.

As the next Step we need the OAuth Token.
For that please visit following Website:

https://twitchtokengenerator.com/

We now want create Custom Bot.

Currently following Options are needed for full function:
- chat:read
- chat:edit
- channel:read:subscriptions
- channel:manage:predictions
- channel:read:predictions

Quick Link with the settings: https://twitchtokengenerator.com/quick/cy9uJ5yT7S

There could be more in the future, always read the Update Informations!
Also some of the permissions aren't needed currently, but will be in the future!

Save the Access Token (Your OAuth Key), Refresh Token and Client ID (To manage the settings) and dont give them to anybody else!

If you got all the keys, then you can look into the config. In the **Options.Twitch** Section you can change **JacksonUndercover** to your Twitch Username.
- IngameName - is your Minecraft username
- ClientId - the created Twitch Application Client Id
- ClientSecret - the created Twitch Application Client Secret
- OAuth - the AccessToken from the twitchtokengenerator Website

This was everything, if you only want to use the Subscription/Follow/Raid feature.

If you want to use Channel Points, then you need to set them up on your Twitch Account, with the exact name in the config (or change at least the config name).


