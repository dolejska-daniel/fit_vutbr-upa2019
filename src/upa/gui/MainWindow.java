package upa.gui;

import upa.Application;
import upa.db.Connection;
import upa.gui.model.EntryTableModel;

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
    private JList imageList;
    private JScrollPane imageDisplay;
    private JList geometryList;
    private JScrollPane geometryDisplay;
    private JButton removeEntryButton;

    private int selectedEntryIndex = -1;

    public MainWindow()
    {
        Application.connection = Connection.CreateConnection();

        setLocationRelativeTo(null);
        setContentPane(contentPane);
        setSize(800, 600);

        entriesTable.setModel(new EntryTableModel());
        entriesTable.doLayout();
        entriesTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;

            final DefaultListSelectionModel target = (DefaultListSelectionModel) e.getSource();
            selectedEntryIndex = target.getAnchorSelectionIndex();
            if (!removeEntryButton.isEnabled())
                removeEntryButton.setEnabled(true);
        });

        newEntryButton.addActionListener(e -> WindowManager.ShowNewEntryDialog());
        removeEntryButton.addActionListener(e -> {
            GetEntryTableModel().Delete(selectedEntryIndex);
            selectedEntryIndex = -1;
            removeEntryButton.setEnabled(false);
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

    public EntryTableModel GetEntryTableModel()
    {
        return (EntryTableModel) entriesTable.getModel();
    }

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
