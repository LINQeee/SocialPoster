
# Social Poster Telegram bot

[![Docker Pulls](https://img.shields.io/docker/pulls/adrianvved/social-poster-bot?logo=docker)](https://hub.docker.com/r/adrianvved/social-poster-bot)
[![Discord](https://discord.com/api/guilds/1121926855545593978/widget.png?style=shield)](https://discord.gg/wc6pFPfjvg)

A great bot that will help you upload short videos to Instagram, Facebook, YouTube and telegram


## READ THIS

- **facebook api requires https protocol**, so this repo **includes self-signed localhost ssl certificate**
- if you have any question **read FAQ** at first, if you **still have any question message me on my discord server**


## Usage

#### You can use the bot in two ways:
- clone the repo and run it locally
- run the docker container

### Run bot locally
1. First of all you need to clone the repository
```bash
git clone https://github.com/LINQeee/SocialPoster
```
2. Change variables in application.properties file:
   
- `SERVER_BASE_URL` - url to bots' endpoints (`https://localhost:9292` by default)
- `BOT_NAME` - your telegram bots' name
- `BOT_TOKEN` - your telegram bots' token
- `GOOGLE_CLIENT_ID` - your google applications' client id
- `GOOGLE_CLIENT_SECRET` - your google applications' client secret
- `FACEBOOK_CLIENT_ID` - your facebook applications' client id
- `FACEBOOK_CLIENT_SECRET` - your facebook applications' client secret
- `DATABASE_IP` - your database ip (`localhost` by default)
- `DATABASE_PORT` - your database port (`3306` by default)
- `DATABASE_NAME` - name of your database
- `DATABASE_USERNAME` - username of your database
- `DATABASE_PASSWORD` - password of your database

3. Then you can run it through an ide or build and run the jar.
   To build and run the project use this commands:
```bash
cd SocialPoster
gradlew build
cd build/libs
java -jar social_poster-0.0.1-RELEASE.jar
```
## Run docker container

Go to https://hub.docker.com/repository/docker/adrianvved/social-poster-bot/general and run docker container using container description guide

## FAQ

#### How to get bot token/name?
- Go to telegram and message @BotFather

- Using `/newbot` command create bot name and receive your bot token

#### How to get google client id/secret?
- Go to https://console.cloud.google.com/apis/dashboard and create new project with `YouTube Data API V3`

- Go to Credentials > Create Credentials > OAuth Client ID > choose web application > add to authorized redirect URIs your `server base url + /google-auth` (`https://localhost:9292/google-auth` by default) > save

- Publish your app to let users login through your telegram

#### How to get facebook client id/secret?
- Go to https://developers.facebook.com/apps, press create app, pick other and finally pick company, finish creation

-  On your app dashboard choose add `api graph for instagram`

- Go to application settings > basics there are your facebook token and id

#### Instagram doesn't work
- Make sure you have added api graph for instagram in yout facebook application dashboard

#### Google doesn't work
- Make sure you have published your google app (go to APIs and Services page of your application, then go to OAuth consent screen and press `publish`)


## License

MIT License

Copyright 2023 Vvedenskii Adrian

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

