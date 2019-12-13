package upa.gui;

import upa.Application;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

public class InitializeDb extends JDialog
{
    private JPanel contentPane;
    private JButton createStructureButton;
    private JButton buttonCancel;
    private JButton createStructureWithDummyButton;

    public InitializeDb()
    {
        //-----------------------------------------------------dd--
        //  Window setup
        //-----------------------------------------------------dd--

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(createStructureButton);

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
        //  Data setup
        //-----------------------------------------------------dd--


        //-----------------------------------------------------dd--
        //  Listeners setup
        //-----------------------------------------------------dd--

        createStructureButton.addActionListener(e -> {
            createStructure();
            onOK();
        });
        createStructureWithDummyButton.addActionListener(e -> {
            createStructure();
            insertDummyData();
            onOK();
        });

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void createStructure()
    {
        File schemaFile = new File("sql/table-schema.sql");
        if (!schemaFile.exists())
        {
            WindowManager.ShowErrorMessageDialog(String.format("Database schema file '%s' could not be found!", schemaFile.toPath().toAbsolutePath()), "Database schema file not found");
            onCancel();
        }

        try
        {
            processSqlFile(schemaFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            WindowManager.ShowErrorMessageDialog("Failed to process database schema file!\nFollowing exception occured: " + e.getMessage(), "Database schema initialization failed");
            onCancel();
        }
    }

    private void insertDummyData()
    {
        File dataFile = new File("sql/dummy-data.sql");
        if (!dataFile.exists())
        {
            WindowManager.ShowErrorMessageDialog(String.format("Database dummydata file '%s' could not be found!", dataFile.toPath().toAbsolutePath()), "Database dummydata file not found");
            onCancel();
        }

        try
        {
            processSqlFile(dataFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            WindowManager.ShowErrorMessageDialog("Failed to process database dummydata file!\nFollowing exception occured: " + e.getMessage(), "Database dummydata insertion failed");
            onCancel();
        }
    }

    private void processSqlFile(File file) throws IOException, SQLException
    {
        final Connection c = Application.connection;
        final String schemaString = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        final String[] queries = schemaString.split(";");

        c.setAutoCommit(false);
        // length -1 to ignore last empty query
        for (int i = 0; i < queries.length - 1; i++)
            c.prepareStatement(queries[i]).execute();
        c.setAutoCommit(true);
    }

    private void onOK()
    {
        dispose();
    }

    private void onCancel()
    {
        dispose();
    }
}
