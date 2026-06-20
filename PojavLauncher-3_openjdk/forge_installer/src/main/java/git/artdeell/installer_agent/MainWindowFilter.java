package git.artdeell.installer_agent;

import java.awt.Component;
import javax.swing.AbstractButton;

public class MainWindowFilter implements ComponentFilter {
    @Override
    public boolean checkComponent(Component component) {
        return component instanceof AbstractButton;
    }
}
