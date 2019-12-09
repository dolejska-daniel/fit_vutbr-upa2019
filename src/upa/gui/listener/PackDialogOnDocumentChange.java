package upa.gui.listener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 */
public class PackDialogOnDocumentChange implements DocumentListener
{
    private JDialog dialog;

    /**
     * @param dialog Dialog window instance.
     */
    public PackDialogOnDocumentChange(JDialog dialog)
    {
        this.dialog = dialog;
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        this.dialog.pack();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        this.dialog.pack();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
    }
}
