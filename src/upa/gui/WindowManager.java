package upa.gui;

import upa.db.entity.Image;

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

    public static void ShowNewEntryDialog()
    {
        CreateEntry dialog = new CreateEntry();
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);

        if (dialog.createdEntry != null)
            mainWindow.GetEntryTableModel().Insert(dialog.createdEntry);
    }

    public static void ShowNewImageDialog(final int entry_id)
    {
        CreateImage dialog = new CreateImage(entry_id);
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);

        if (dialog.createdImage != null)
            mainWindow.GetImageTableModel().Insert(dialog.createdImage);
    }

    public static void ShowEditImageDialog(final Image image)
    {
        EditImage dialog = new EditImage(image);
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);

        mainWindow.ReloadImageToBeDisplayed();
        mainWindow.repaint();
    }
}
