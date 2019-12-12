package upa.gui.listener;

import upa.gui.MainWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GeometryRectMouseListener extends MouseAdapter
{
    private MainWindow mainWindow;

    private Point sourcePoint;
    private Rectangle rectangle;

    public GeometryRectMouseListener(MainWindow mainWindow)
    {
        super();
        this.mainWindow = mainWindow;
    }

    private void CreateNewShape(Point point)
    {
        sourcePoint = point;
        rectangle = new Rectangle(point);
        mainWindow.SetActiveGeometry(rectangle);
    }

    private void DestroyActiveShape()
    {
        mainWindow.RemoveActiveGeometry();
        rectangle = null;
        sourcePoint = null;
    }

    private void SaveActiveShape()
    {
        mainWindow.SaveActiveGeometry();
        rectangle = null;
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
        if (rectangle == null || sourcePoint == null)
            return;

        int distanceX = e.getX() - (int) sourcePoint.getX();
        int distanceY = e.getY() - (int) sourcePoint.getY();
        rectangle.setSize(Math.abs(distanceX), Math.abs(distanceY));

        distanceX *= -(-1 + (int) Math.signum(distanceX)) >> 1;
        distanceY *= -(-1 + (int) Math.signum(distanceY)) >> 1;
        rectangle.setLocation(sourcePoint.x + distanceX, sourcePoint.y + distanceY);

        mainWindow.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        SaveActiveShape();
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        DestroyActiveShape();
    }
}
