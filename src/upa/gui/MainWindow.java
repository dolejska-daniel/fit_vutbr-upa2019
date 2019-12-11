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
    private JTabbedPane tabbedPane;
    private JScrollPane imageDisplayPane;
    private JScrollPane geometryDisplayPane;
    private JButton removeEntryButton;
    private JTable imagesTable;
    private JButton newImageButton;
    private JButton removeImageButton;
    private JTable geometryTable;
    private JButton button1;
    private JButton button2;
    private JButton calculateXXXaButton;
    private JButton calculateXXXbButton;
    private JButton calculateXXXcButton;
    private JButton editImageButton;
    private JButton findSimilarButton;

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
        setSize(1200, 600);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

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
            if (target.isSelectionEmpty())
                ClearEntrySelection();
            else
                SaveEntrySelection(target.getAnchorSelectionIndex());
        });
        entriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //-----------------------------------------------------dd--
        //  Entries buttons setup
        //-----------------------------------------------------dd--

        newEntryButton.addActionListener(e -> WindowManager.ShowNewEntryDialog());
        removeEntryButton.addActionListener(e -> {
            GetEntryTableModel().Delete(selectedEntryIndex);
            entriesTable.getSelectionModel().clearSelection();
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
            if (target.isSelectionEmpty())
                ClearImageSelection();
            else
                SaveImageSelection(target.getAnchorSelectionIndex());
        });
        imagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //-----------------------------------------------------dd--
        //  Image buttons setup
        //-----------------------------------------------------dd--

        newImageButton.addActionListener(e -> WindowManager.ShowNewImageDialog(selectedEntry.id));
        removeImageButton.addActionListener(e -> {
            GetImageTableModel().Delete(selectedEntryIndex);
            imagesTable.getSelectionModel().clearSelection();
        });
        editImageButton.addActionListener(e -> WindowManager.ShowEditImageDialog(selectedImage));
        // findSimilarButton.addActionListener(e -> WindowManager.ShowFindSimilarImagesDialog(selectedImage));

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

        // enable relevant objects
        removeEntryButton.setEnabled(true);
        newImageButton.setEnabled(true);
        tabbedPane.setEnabled(true);

        // reload image table
        GetImageTableModel().SetEntryId(selectedEntry.id);
        // TODO: Reload geometry table
    }

    private void ClearEntrySelection()
    {
        selectedEntryIndex = -1;
        selectedEntry = null;

        // disable relevant objects
        removeEntryButton.setEnabled(false);
        newImageButton.setEnabled(false);
        tabbedPane.setEnabled(false);

        // reload image table
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
        editImageButton.setEnabled(true);
        findSimilarButton.setEnabled(true);
        removeImageButton.setEnabled(true);

        try
        {
            imageToBeDisplayed = ImageIO.read(selectedImage.image.getDataInStream());
            imageDisplay.setPreferredSize(new Dimension(imageToBeDisplayed.getWidth(), imageToBeDisplayed.getHeight()));

            setSize(getWidth() + 1, getHeight() + 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void ClearImageSelection()
    {
        selectedImageIndex = -1;
        selectedImage = null;

        // disable remove button
        editImageButton.setEnabled(false);
        findSimilarButton.setEnabled(false);
        removeImageButton.setEnabled(false);

        imageToBeDisplayed = null;
        imageDisplay.setPreferredSize(new Dimension(1, 1));

        setSize(getWidth() - 1, getHeight() - 1);
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
