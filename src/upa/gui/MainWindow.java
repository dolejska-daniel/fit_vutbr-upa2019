package upa.gui;

import upa.Application;
import upa.db.Connection;
import upa.gui.model.EntryTableModel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JDialog
{
    private JPanel contentPane;
    private JPanel Controls;
    private JButton button1;
    private JTable entriesTable;
    private JTabbedPane tabbedPane1;
    private JList imageList;
    private JScrollPane imageDisplay;
    private JList geometryList;
    private JScrollPane geometryDisplay;

    public MainWindow()
    {
        Application.connection = Connection.CreateConnection();

        setContentPane(contentPane);
        setSize(800, 600);

        entriesTable.setModel(new EntryTableModel());
        entriesTable.doLayout();

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
