package gui;

import javax.swing.*;

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
    }
}
