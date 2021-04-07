package ubc.cosc322;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

public class Entry {
    public static void main(String[] args)
    {
        String socket = "";
        if(args.length > 0) socket = args[0];
        AmazonPlayer player = new AmazonPlayer("okgntech2", "W3 W1LL D0M1N&Te", socket);
        while(player.running){}
    }
}
