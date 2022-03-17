# TwitchViewerControl
Twitch Viewers can Control your Minecraft

Short help, will be edited:

Create an app here:
https://dev.twitch.tv/console/apps/create

- Name can be anything, but dont use 'Twitch' in the name, since it wont work then
- Redirect URL can be "https://localhost" if you dont know, what to do there.
- Category is a Game Integration.

After you have done the Captcha and clicked on save, you need to go back in the settigns with "manage".
There will be now a Client-ID.
You will also need the Client-Secret. Press on new Secret to generate one.

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

Quick Link: https://twitchtokengenerator.com/quick/cy9uJ5yT7S

There could be more in the future, always read the Update Informations!
Also some of them aren't needed currently but will be in the future!

Save the Access Token (Your OAuth Key), Refresh Token and Client ID (To manage the settings) and dont give them to anybody else!


