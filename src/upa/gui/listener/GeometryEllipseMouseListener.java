package upa.gui.listener;

import upa.gui.MainWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class GeometryEllipseMouseListener extends MouseAdapter
{
    private MainWindow mainWindow;

    private Point sourcePoint;
    private Ellipse2D ellipse;

    public GeometryEllipseMouseListener(MainWindow mainWindow)
    {
        super();
        this.mainWindow = mainWindow;
    }

    private void CreateNewShape(Point point)
    {
        sourcePoint = point;
        ellipse = new Ellipse2D.Double(point.getX(), point.getY(), 0, 0);
        mainWindow.SetActiveGeometry(ellipse);
    }

    private void DestroyActiveShape()
    {
        mainWindow.RemoveActiveGeometry();
        ellipse = null;
        sourcePoint = null;
    }

    private void SaveActiveShape()
    {
        mainWindow.SaveActiveGeometry();
        ellipse = null;
        sourcePoint = null;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        CreateNewShape(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (ellipse == null || sourcePoint == null)
            return;

        ellipse.setFrameFromCenter(ellipse.getCenterX(), ellipse.getCenterY(), e.getX(), e.getY());
        mainWindow.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (ellipse.getWidth() == 0 || ellipse.getHeight() == 0)
        {
            DestroyActiveShape();
            return;
        }

        SaveActiveShape();
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        DestroyActiveShape();
    }
}
