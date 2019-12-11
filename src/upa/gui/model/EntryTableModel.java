package upa.gui.model;

import upa.db.entity.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Table model for Entry database entity.
 *
 * @author Daniel Dolejška
 * @since 2019-12-09
 */
public class EntryTableModel extends BaseTableModel
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


    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    public EntryTableModel()
    {
        entries = Entry.GetAll();
    }


    //=====================================================================dd==
    // CUSTOM HELPER METHODS
    //=====================================================================dd==

    public Entry Get(final int rowIndex)
    {
        return entries.get(rowIndex);
    }

    public void Insert(Entry e)
    {
        entries.add(e);
        NotifyTableInsert(entries.size() - 1);
    }

    public void Delete(int rowIndex)
    {
        if (rowIndex < 0)
            return;

        Entry e = entries.get(rowIndex);
        e.Delete();

        entries.remove(rowIndex);
        NotifyTableDelete(rowIndex);
    }


    //=====================================================================dd==
    // OVERRIDE TABLE MODEL METHODS
    //=====================================================================dd==

    //<editor-fold desc="Override table model methods">
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
        return columnIndex > 0 && this.isEditable;
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
        Entry entry = entries.get(rowIndex);
        switch (columnIndex)
        {
            case 0:
                entry.id = (Integer) aValue;
                break;
            case 1:
                assert aValue instanceof String;
                entry.name = (String) aValue;
                break;
            case 2:
                assert aValue instanceof String;
                entry.description = (String) aValue;
                break;
        }

        entry.Update();
        NotifyTableUpdate(rowIndex, columnIndex);
    }
    //</editor-fold>
}
