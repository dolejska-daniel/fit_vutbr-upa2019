package upa.gui;

import upa.Application;
import upa.db.Connection;
import upa.db.entity.Entry;
import upa.db.entity.Image;
import upa.gui.model.EntryTableModel;
import upa.gui.model.ImageTableModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class MainWindow extends JDialog
{
    private JPanel contentPane;
    private JPanel Controls;
    private JButton newEntryButton;
    private JTable entriesTable;
    private JTabbedPane tabbedPane1;
    private JScrollPane imageDisplayPane;
    private JList geometryList;
    private JScrollPane geometryDisplay;
    private JButton removeEntryButton;
    private JTable imagesTable;
    private JButton newImageButton;
    private JButton removeImageButton;

    private JPanel imageDisplayWrapper;
    private JPanel imageDisplay;
    private BufferedImage imageToBeDisplayed;

    private int selectedEntryIndex = -1;
    private Entry selectedEntry = null;

    private int selectedImageIndex = -1;
    private Image selectedImage = null;


    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    public MainWindow()
    {
        Application.connection = Connection.CreateConnection();

        setLocationRelativeTo(null);
        setContentPane(contentPane);
        setSize(800, 600);

        //-----------------------------------------------------dd--
        //  Entries table setup
        //-----------------------------------------------------dd--

        entriesTable.setModel(new EntryTableModel());
        entriesTable.doLayout();
        entriesTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;

            final DefaultListSelectionModel target = (DefaultListSelectionModel) e.getSource();
            // save current selection
            SaveEntrySelection(target.getAnchorSelectionIndex());
        });

        //-----------------------------------------------------dd--
        //  Entries buttons setup
        //-----------------------------------------------------dd--

        newEntryButton.addActionListener(e -> WindowManager.ShowNewEntryDialog());
        removeEntryButton.addActionListener(e -> {
            GetEntryTableModel().Delete(selectedEntryIndex);
            ClearEntrySelection();
        });

        //-----------------------------------------------------dd--
        //  Images table setup
        //-----------------------------------------------------dd--

        imagesTable.setModel(new ImageTableModel());
        imagesTable.doLayout();
        imagesTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;

            final DefaultListSelectionModel target = (DefaultListSelectionModel) e.getSource();
            // save current selection
            SaveImageSelection(target.getAnchorSelectionIndex());
        });

        //-----------------------------------------------------dd--
        //  Image buttons setup
        //-----------------------------------------------------dd--

        newImageButton.addActionListener(e -> WindowManager.ShowNewImageDialog(selectedEntry.id));
        removeImageButton.addActionListener(e -> {
            GetImageTableModel().Delete(selectedEntryIndex);
            ClearImageSelection();
        });

        //-----------------------------------------------------dd--
        //  Image canvas setup
        //-----------------------------------------------------dd--

        imageDisplay = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.drawImage(imageToBeDisplayed, 0, 0, null);
            }
        };

        imageDisplayPane.getHorizontalScrollBar().setUnitIncrement(16);
        imageDisplayPane.getVerticalScrollBar().setUnitIncrement(16);
        imageDisplayPane.setViewportView(imageDisplay);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });
    }


    //=====================================================================dd==
    // CUSTOM HELPER METHODS
    //=====================================================================dd==

    //-----------------------------------------------------dd--
    //  Entries table methods
    //-----------------------------------------------------dd--

    public EntryTableModel GetEntryTableModel()
    {
        return (EntryTableModel) entriesTable.getModel();
    }

    private void SaveEntrySelection(final int index)
    {
        selectedEntryIndex = index;
        selectedEntry = GetEntryTableModel().Get(index);

        // enable relevant buttons
        removeEntryButton.setEnabled(true);
        newImageButton.setEnabled(true);

        // reload image table
        ClearImageSelection();
        GetImageTableModel().SetEntryId(selectedEntry.id);
        // TODO: Reload geometry table
    }

    private void ClearEntrySelection()
    {
        entriesTable.getSelectionModel().clearSelection();
        selectedEntryIndex = -1;
        selectedEntry = null;

        // disable relevant buttons
        removeEntryButton.setEnabled(false);
        newImageButton.setEnabled(false);

        // reload image table
        ClearImageSelection();
        GetImageTableModel().SetEntryId(-1);
        // TODO: Reload geometry table
    }

    //-----------------------------------------------------dd--
    //  Images table methods
    //-----------------------------------------------------dd--

    public ImageTableModel GetImageTableModel()
    {
        return (ImageTableModel) imagesTable.getModel();
    }

    private void SaveImageSelection(final int index)
    {
        selectedImageIndex = index;
        selectedImage = GetImageTableModel().Get(index);

        // enable remove button
        removeImageButton.setEnabled(true);

        try
        {
            imageToBeDisplayed = ImageIO.read(selectedImage.image.getDataInStream());
            imageDisplay.setPreferredSize(new Dimension(imageToBeDisplayed.getWidth(), imageToBeDisplayed.getHeight()));

            setSize(getWidth() + 1, getHeight() + 1);
            //setSize(getWidth() - 1, getHeight() - 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void ClearImageSelection()
    {
        imagesTable.getSelectionModel().clearSelection();
        selectedImageIndex = -1;
        selectedImage = null;

        // disable remove button
        removeImageButton.setEnabled(false);
    }


    //=====================================================================dd==
    // CUSTOM HELPER METHODS
    //=====================================================================dd==

    private void onOK()
    {
        dispose();
    }

    private void onCancel()
    {
        Connection.CloseConnection(Application.connection);

        dispose();
    }
}
