package upa.gui.listener;

import upa.db.entity.Geometry;
import upa.gui.MainWindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SelectionGeometryMouseListener extends MouseAdapter
{
    private MainWindow mainWindow;

    public SelectionGeometryMouseListener(MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (mainWindow.HasActiveGeometryListener())
            return;

        boolean found = false;
        List<Geometry> geometryList = mainWindow.GetGeometryTableModel().GetGeometryList();
        for (int i = geometryList.size() - 1; i >= 0; --i)
        {
            Geometry geometry = geometryList.get(i);
            if (geometry.Shape().contains(e.getPoint()))
            {
                mainWindow.GetGeometryTable().getSelectionModel().setSelectionInterval(i, i);
                found = true;
                break;
            }
        }

        if (!found)
            mainWindow.GetGeometryTable().getSelectionModel().clearSelection();
    }
}
