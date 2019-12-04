package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI window class.
 *
 * @author Daniel Dolejška
 * @since 2019-12-04
 */
public class MainWindow extends JFrame
{
    /**
     * Create and configure instance of program's main window.
     */
    public MainWindow()
    {
        super("[UPA] Realitní kancl č.69", WindowManager.graphicsCfg);

        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.SetupLayout();
    }

    private void SetupLayout()
    {
        this.setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        this.add(leftPanel, BorderLayout.WEST);
        leftPanel.add(new JButton("Hello worlds1."));
        leftPanel.add(new JButton("Hello worlds2."));
        leftPanel.add(new JButton("Hello worlds3."));
        leftPanel.add(new JButton("Hello worlds4."));
        leftPanel.add(new JButton("Hello worlds5."));
        leftPanel.add(new JButton("Hello worlds6."));

        JPanel rightPanel = new JPanel();
        this.add(rightPanel, BorderLayout.CENTER);
    }
}
