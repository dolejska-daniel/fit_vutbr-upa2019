package upa.gui.model;

import upa.db.entity.Image;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for Image database entity.
 *
 * @author Daniel Dolejška
 * @since 2019-12-09
 */
public class ImageTableModel extends BaseTableModel
{
    List<Image> images;

    List<String> columns = new ArrayList<>()
    {
        {
            add("ID");
            add("Title");
            add("Description");
            // add("Icon");
        }
    };


    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    /**
     * Creates and initializes Image table model.
     */
    public ImageTableModel()
    {
        images = new ArrayList<>();
    }


    //=====================================================================dd==
    // CUSTOM HELPER METHODS
    //=====================================================================dd==

    public void SetEntryId(final int entry_id)
    {
        if (entry_id == -1)
            return;

        SetImagesList(Image.GetAll(entry_id));
    }

    public void SetImagesList(List<Image> images)
    {
        int imagesCount = getRowCount();
        while (imagesCount-- != 0)
        {
            Remove(0);
        }

        this.images = images;
        NotifyTableReload(0, images.size() - 1);
    }

    public Image Get(final int rowIndex)
    {
        return images.get(rowIndex);
    }

    public void Insert(Image i)
    {
        images.add(i);
        NotifyTableInsert(images.size() - 1);
    }

    public void Remove(int rowIndex)
    {
        if (rowIndex < 0)
            return;

        images.remove(rowIndex);
        NotifyTableDelete(rowIndex);
    }

    public void Delete(int rowIndex)
    {
        if (rowIndex < 0)
            return;

        Image i = images.get(rowIndex);
        i.Delete();
        Remove(rowIndex);
    }


    //=====================================================================dd==
    // OVERRIDE TABLE MODEL METHODS
    //=====================================================================dd==

    //<editor-fold desc="Override table model methods">
    @Override
    public int getRowCount()
    {
        return images.size();
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
            case 3:
                return ImageIcon.class;

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
        Image image = images.get(rowIndex);
        switch (columnIndex)
        {
            case 0:
                return image.id;
            case 1:
                return image.title;
            case 2:
                return image.description;
            case 3:
                try
                {
                    return new ImageIcon(image.image.getDataInByteArray(), image.title);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return new ImageIcon();
                }

            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        Image image = images.get(rowIndex);
        switch (columnIndex)
        {
            case 0:
                image.id = (Integer) aValue;
                break;
            case 1:
                assert aValue instanceof String;
                image.title = (String) aValue;
                break;
            case 2:
                assert aValue instanceof String;
                image.description = (String) aValue;
                break;

            default:
                return;
        }

        image.Update();
        NotifyTableUpdate(rowIndex, columnIndex);
    }
    //</editor-fold>
}
