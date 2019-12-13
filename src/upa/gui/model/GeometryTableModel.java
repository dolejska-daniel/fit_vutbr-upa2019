package upa.gui.model;

import upa.db.entity.Geometry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Table model for Geometry database entity.
 *
 * @author Daniel Dolejška
 * @since 2019-12-12
 */
public class GeometryTableModel extends BaseTableModel
{
    List<Geometry> geometry;

    List<String> columns = new ArrayList<>()
    {
        {
            add("ID");
            add("Type");
        }
    };


    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    /**
     * Creates and initializes Image table model.
     */
    public GeometryTableModel()
    {
        geometry = new ArrayList<>();
    }


    //=====================================================================dd==
    // CUSTOM HELPER METHODS
    //=====================================================================dd==

    public void SetEntryId(final int entry_id)
    {
        if (entry_id == -1)
            return;

        SetGeometryList(Geometry.GetAll(entry_id));
    }

    public void SetGeometryList(List<Geometry> geometry)
    {
        int imagesCount = getRowCount();
        while (imagesCount-- != 0)
        {
            Remove(0);
        }

        this.geometry = geometry;
        NotifyTableReload(0, geometry.size() - 1);
    }

    public List<Geometry> GetGeometryList()
    {
        return this.geometry;
    }

    public Geometry Get(final int rowIndex)
    {
        return geometry.get(rowIndex);
    }

    public void Insert(Geometry g)
    {
        geometry.add(g);
        NotifyTableInsert(geometry.size() - 1);
    }

    public void Remove(int rowIndex)
    {
        if (rowIndex < 0)
            return;

        geometry.remove(rowIndex);
        NotifyTableDelete(rowIndex);
    }

    public void Delete(int rowIndex)
    {
        if (rowIndex < 0)
            return;

        Geometry geometry = this.geometry.get(rowIndex);
        geometry.Delete();
        Remove(rowIndex);
    }

    public void Sort()
    {
        geometry.sort(Comparator.comparingInt(o -> o.layer));
    }


    //=====================================================================dd==
    // OVERRIDE TABLE MODEL METHODS
    //=====================================================================dd==

    //<editor-fold desc="Override table model methods">
    @Override
    public int getRowCount()
    {
        return geometry.size();
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
                return String.class;

            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return columnIndex > 0 && columnIndex != 3 && this.isEditable;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Geometry geometry = this.geometry.get(rowIndex);
        switch (columnIndex)
        {
            case 0:
                return geometry.id;
            case 1:
                return geometry.type;

            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        Geometry geometry = this.geometry.get(rowIndex);
        switch (columnIndex)
        {
            case 0:
                geometry.id = (Integer) aValue;
                break;
            case 1:
                assert aValue instanceof String;
                geometry.type = (String) aValue;
                break;

            default:
                return;
        }

        geometry.Update();
        NotifyTableUpdate(rowIndex, columnIndex);
    }
    //</editor-fold>
}
