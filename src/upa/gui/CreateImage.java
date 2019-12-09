package upa.gui;

import upa.db.entity.Image;
import upa.gui.listener.PackDialogOnDocumentChange;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;

public class CreateImage extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField titleField;
    private JTextArea descriptionField;
    private JTextField entryField;
    private JButton buttonSelectFile;
    private JTextField imagePathField;
    private File imageFile;
    private JLabel errorLabel;

    public CreateImage(final int entry_id)
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        entryField.setText(String.valueOf(entry_id));
        buttonSelectFile.addActionListener(e -> SelectImageFile());

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

    private void SelectImageFile()
    {
        JFileChooser dialog = new JFileChooser();
        dialog.setDialogType(JFileChooser.OPEN_DIALOG);
        dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dialog.setMultiSelectionEnabled(false);
        dialog.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                if (f.isDirectory())
                    return true;

                try
                {
                    String mimetype = Files.probeContentType(f.toPath());
                    return mimetype.split("/")[0].equals("image");
                }
                catch (Exception e)
                {
                    return false;
                }
            }

            @Override
            public String getDescription()
            {
                return null;
            }
        });
        dialog.showDialog(this, "Select");

        File file = dialog.getSelectedFile();
        this.imagePathField.setText(file.toPath().toAbsolutePath().toString());
        this.imageFile = file;
    }

    private void RegisterListeners()
    {
        //---------------------------------------------dd--
        // Dialog resize listeners
        //---------------------------------------------dd--
        entryField.getDocument().addDocumentListener(new PackDialogOnDocumentChange(this));
        titleField.getDocument().addDocumentListener(new PackDialogOnDocumentChange(this));
        descriptionField.getDocument().addDocumentListener(new PackDialogOnDocumentChange(this));
        imagePathField.getDocument().addDocumentListener(new PackDialogOnDocumentChange(this));
    }

    private void onOK()
    {
        try
        {
            Image i = new Image();

            Document entry = entryField.getDocument();
            i.entry_id = Integer.parseInt(entry.getText(0, entry.getLength()));

            Document title = titleField.getDocument();
            i.title = title.getText(0, title.getLength());

            Document description = descriptionField.getDocument();
            i.description = description.getText(0, description.getLength());

            i.Create();
            i.LoadImage(imageFile.toPath());

            System.out.println("Created new Image: EntryID=" + i.entry_id + ", ID=" + i.id);
        }
        catch (Exception e)
        {
            errorLabel.setText(e.getCause().getMessage());
            pack();

            e.printStackTrace();
            return;
        }

        dispose();
    }

    private void onCancel()
    {
        // add your code here if necessary
        dispose();
    }
}
