package git.artdeell.installer_agent;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class DialogFilter implements ComponentFilter {
    @Override
    public boolean checkComponent(Component component) {
        return component instanceof JOptionPane
                || component instanceof JProgressBar;
    }
}
