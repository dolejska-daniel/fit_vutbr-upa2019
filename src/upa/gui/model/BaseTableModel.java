package upa.gui.model;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Dolejška
 * @since 2019-12-10
 */
public abstract class BaseTableModel implements TableModel
{
    private List<TableModelListener> listeners = new ArrayList<>();

    protected void NotifyTableUpdate(final int rowIndex, final int columnIndex)
    {
        TableModelEvent event = new TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE);
        listeners.forEach(listener -> listener.tableChanged(event));
    }

    protected void NotifyTableReload(final int firstRowIndex, final int lastRowIndex)
    {
        TableModelEvent event = new TableModelEvent(this, firstRowIndex, lastRowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
        listeners.forEach(listener -> listener.tableChanged(event));
    }

    protected void NotifyTableInsert(final int rowIndex)
    {
        TableModelEvent event = new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        listeners.forEach(listener -> listener.tableChanged(event));
    }

    protected void NotifyTableDelete(final int rowIndex)
    {
        TableModelEvent event = new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
        listeners.forEach(listener -> listener.tableChanged(event));
    }

    protected void NotifyTableDelete(final int firstRowIndex, final int lastRowIndex)
    {
        TableModelEvent event = new TableModelEvent(this, firstRowIndex, lastRowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
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
}
