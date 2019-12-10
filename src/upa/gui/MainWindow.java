package upa.gui;

import upa.Application;
import upa.db.Connection;
import upa.db.entity.Entry;
import upa.gui.model.EntryTableModel;
import upa.gui.model.ImageTableModel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JDialog
{
    private JPanel contentPane;
    private JPanel Controls;
    private JButton newEntryButton;
    private JTable entriesTable;
    private JTabbedPane tabbedPane1;
    private JScrollPane imageDisplay;
    private JList geometryList;
    private JScrollPane geometryDisplay;
    private JButton removeEntryButton;
    private JTable imagesTable;

    private int selectedEntryIndex = -1;
    private Entry selectedEntry = null;


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

        // enable remove button
        removeEntryButton.setEnabled(true);
        // reload image table
        GetImageTableModel().SetEntryId(selectedEntry.id);
        // TODO: Reload geometry table
    }

    private void ClearEntrySelection()
    {
        selectedEntryIndex = -1;
        selectedEntry = null;

        // disable remove button
        removeEntryButton.setEnabled(false);
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
        selectedEntryIndex = index;
        selectedEntry = GetEntryTableModel().Get(index);

        // enable remove button
        removeEntryButton.setEnabled(true);
    }

    private void ClearImageSelection()
    {
        selectedEntryIndex = -1;
        selectedEntry = null;

        // disable remove button
        removeEntryButton.setEnabled(false);
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
