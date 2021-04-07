package ubc.cosc322.messages;

public class MCTSUpdateMessage extends BaseMessage {
    public MCTSUpdate[] updates;

    public MCTSUpdateMessage(MCTSUpdate[] updates) {
        message_type = MessageType.MCTS_UPDATE_MESSAGE;
        this.updates = updates;
    }
}
