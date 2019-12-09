package upa.gui.model;

import upa.db.entity.Entry;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EntryTableModel implements TableModel
{
    List<Entry> entries;

    List<String> columns = new ArrayList<>()
    {
        {
            add("ID");
            add("Name");
            add("Description");
        }
    };

    /**
     *
     */
    public EntryTableModel()
    {
        entries = Entry.GetAll();
    }

    @Override
    public int getRowCount()
    {
        return entries.size();
    }

    @Override
    public int getColumnCount()
    {
        return columns.size();
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        return columns.get(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
                return Integer.class;
            case 1:
            case 2:
                return String.class;

            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Entry entry = entries.get(rowIndex);
        switch (columnIndex)
        {
            case 0:
                return entry.id;
            case 1:
                return entry.name;
            case 2:
                return entry.description;

            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {

    }

    @Override
    public void addTableModelListener(TableModelListener l)
    {

    }

    @Override
    public void removeTableModelListener(TableModelListener l)
    {

    }
}
