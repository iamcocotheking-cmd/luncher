package git.artdeell.installer_agent;

import java.util.TimerTask;

public class ComponentTimeoutTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("Forge/OptiFine installer UI was not detected in time.");
        System.exit(1);
    }
}
