package tool.slack

class State {
    String createdAt
    String readFrom
    String readTo
    // 一度全体から取得済みだったらtrue
    boolean alreadyProceededFromChannelCreated
}
