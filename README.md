# slack attachment downloader

Slackの指定したチャネルにアップされている添付ファイルを、指定したディレクトリへダウンロードします。

## セットアップ

### Slack API

https://api.slack.com/apps

`Create New App` から、アプリ名と使用するworkspaceを選択する。


`Add features and functionality` > `Permissions`

`Scopes` セクションで、

- `channels.history`
- `files:read`
- `groups:history`
- `groups:read`

を追加します。

`OAuth Tokens & Redirect URLs` から `Install App to Workspace` します。

`OAuth Access Token` に文字列が表示されるようになるので、この文字列を記録します。(後で使います)


### docker build

```
docker build -t xxx/slack-attachment-downloader .
```

## 使い方

環境変数 `TOKEN`, `CHANNEL`, `PUBLIC` を指定してください。

- `TOKEN` ... slackのサイトで作成したOAuth Tokenです。xoxp-xxxxx〜 という形式です。
- `CHANNEL` ... 取得対象のチャネルIDです。チャネルURLの末尾の文字列です。(https://workspacename.slack.com/messages/XXXXXXXXX ← ここ)
- `PUBLIC` ... 公開チャネルならtrueを、プライベートチャネルならfalseを指定します。

```
docker run --rm -it \
    -e TOKEN=xoxp-YOUR_TOKEN \
    -e CHANNEL=YOUE_CHANNEL_ID \
    -e PUBLIC=false \
    -v $(PWD)/downloads:/app/downloads \
    -v $(PWD)/out/log:/app/log \
    xxx/slack-attachment-downloader \
    java -jar build/libs/slack-attachment-downloader-all.jar
```
