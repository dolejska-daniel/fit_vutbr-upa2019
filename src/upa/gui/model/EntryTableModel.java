package upa.gui.model;

import upa.db.entity.Entry;

import javax.swing.event.TableModelEvent;
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

    List<TableModelListener> listeners = new ArrayList<>();

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
        return columnIndex > 0;
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

        TableModelEvent event = new TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE);
        listeners.forEach(listener -> listener.tableChanged(event));
    }

    @Override
    public void addTableModelListener(TableModelListener l)
    {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l)
    {
        listeners.remove(l);
    }

    public void Insert(Entry e)
    {
        entries.add(e);

        TableModelEvent event = new TableModelEvent(this, entries.size() - 1, entries.size() - 1, 0, TableModelEvent.INSERT);
        listeners.forEach(listener -> listener.tableChanged(event));
    }
}
