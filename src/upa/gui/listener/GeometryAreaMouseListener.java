package upa.gui.listener;

import upa.gui.MainWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GeometryAreaMouseListener extends MouseAdapter
{
    private MainWindow mainWindow;

    private Point sourcePoint;
    private Polygon polygon;

    public GeometryAreaMouseListener(MainWindow mainWindow)
    {
        super();
        this.mainWindow = mainWindow;
    }

    private void CreateNewShape(Point point)
    {
        sourcePoint = point;
        polygon = new Polygon();
        polygon.addPoint(point.x, point.y);
        mainWindow.SetActiveGeometry(polygon);
    }

    private void DestroyActiveShape()
    {
        mainWindow.RemoveActiveGeometry();
        polygon = null;
        sourcePoint = null;
    }

    private void SaveActiveShape()
    {
        mainWindow.SaveActiveGeometry();
        polygon = null;
        sourcePoint = null;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (polygon == null || sourcePoint == null)
        {
            CreateNewShape(e.getPoint());
            return;
        }

        if ((e.getModifiers() & (1 << 2)) != 0)
        {
            // right click
            DestroyActiveShape();
            return;
        }

        polygon.addPoint(e.getX(), e.getY());
        mainWindow.repaint();
        // SaveActiveShape();
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        SaveActiveShape();
    }
}
