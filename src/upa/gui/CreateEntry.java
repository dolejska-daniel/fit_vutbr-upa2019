package upa.gui;

import upa.db.entity.Entry;
import upa.gui.listener.PackDialogOnDocumentChange;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CreateEntry extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameField;
    private JTextArea descriptionField;
    private JLabel errorLabel;

    public CreateEntry()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

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

        RegisterListeners();
    }

    private void RegisterListeners()
    {
        //---------------------------------------------dd--
        // Dialog resize listeners
        //---------------------------------------------dd--
        nameField.getDocument().addDocumentListener(new PackDialogOnDocumentChange(this));
        descriptionField.getDocument().addDocumentListener(new PackDialogOnDocumentChange(this));
    }

    private void onOK()
    {
        try
        {
            Entry e = new Entry();

            Document name = nameField.getDocument();
            e.name = name.getText(0, name.getLength());

            Document description = descriptionField.getDocument();
            e.description = description.getText(0, description.getLength());

            e.Create();
            System.out.println("Created new Entry: ID=" + e.id);
        }
        catch (Exception e)
        {
            errorLabel.setText(e.getMessage());
            pack();

            e.printStackTrace();
            return;
        }

        dispose();
    }

    private void onCancel()
    {
        dispose();
    }
}
