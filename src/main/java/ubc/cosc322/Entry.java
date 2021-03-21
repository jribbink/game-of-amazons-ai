package ubc.cosc322;

import ygraph.ai.smartfox.games.BaseGameGUI;

public class Entry {
    public static void main(String[] args)
    {
        AmazonPlayer player = new AmazonPlayer("okgntech", "W3 W1LL D0M1N&Te");
    	
    	if(player.getGameGUI() == null)
        {
    		player.Go();
    	}
    	else
        {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player.Go();
                }
            });
    	}
    }
}
