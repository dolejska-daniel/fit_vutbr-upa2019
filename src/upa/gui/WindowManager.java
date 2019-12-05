package upa.gui;

import java.awt.*;

/**
 * Window manager - creates and takes care of existing windows.
 *
 * @author Daniel Dolejška
 * @since 2019-12-04
 */
public class WindowManager
{
    /**
     * Window graphics configuration.
     */
    public static GraphicsConfiguration graphicsCfg;

    /**
     * Main window instance.
     */
    public static MainWindow mainWindow;

    /**
     * Create and configure required windows.
     */
    public static void Setup()
    {
        mainWindow = new MainWindow();
    }

    /**
     * Display configured windows.
     */
    public static void Run()
    {
        mainWindow.setVisible(true);
    }
}
