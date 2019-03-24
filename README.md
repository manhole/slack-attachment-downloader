## Slack API

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



## docker

### build

```
docker build -t xxx/slack-attachment-downloader .
```

### run

環境変数 `TOKEN` と `CHANNEL` を指定してください。

```
docker run --rm -it \
    -e TOKEN=xoxp-YOUR_TOKEN \
    -e CHANNEL=YOUE_CHANNEL_ID \
    -v $(PWD)/downloads:/app/downloads \
    -v $(PWD)/out/log:/app/log \
    xxx/slack-attachment-downloader \
    gradle run
```
