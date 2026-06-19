package git.artdeell.installer_agent;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class MainWindowFilter implements ComponentFilter {
    @Override
    public boolean checkComponent(Component component) {
        return component instanceof JRadioButton
                || component instanceof JTextField
                || component instanceof JButton;
    }
}
