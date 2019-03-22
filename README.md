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
