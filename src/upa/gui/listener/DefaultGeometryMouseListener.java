package upa.gui.listener;

import upa.gui.MainWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

public class DefaultGeometryMouseListener extends MouseAdapter
{
    private MainWindow mainWindow;
    private Point sourceLocation;
    private int sourceDistX;
    private int sourceDistY;

    public DefaultGeometryMouseListener(MainWindow mainWindow)
    {

        this.mainWindow = mainWindow;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (!HasSelectedGeometry())
        {
            super.mousePressed(e);
            return;
        }

        final Shape shape = GetSelectedGeometry();
        sourceLocation = shape.getBounds().getLocation();
        sourceDistX = sourceLocation.x - e.getX();
        sourceDistY = sourceLocation.y - e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (!HasSelectedGeometry())
            return;

        final Shape shape = GetSelectedGeometry();
        if (shape instanceof GeneralPath)
        {
            GeneralPath path = (GeneralPath) shape;
            int distX = sourceLocation.x - e.getX();
            int distY = sourceLocation.y - e.getY();

            final AffineTransform transform = new AffineTransform();
            transform.translate(sourceDistX - distX, sourceDistY - distY);

            final Shape newShape = path.createTransformedShape(transform);
            OverwriteSelectedGeometryShape(newShape);
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        if (!HasSelectedGeometry())
            return;
    }

    private boolean HasSelectedGeometry()
    {
        return mainWindow.GetSelectedGeometry() != null;
    }

    private Shape GetSelectedGeometry()
    {
        return mainWindow.GetSelectedGeometry().Shape();
    }

    private void OverwriteSelectedGeometryShape(Shape shape)
    {
        mainWindow.GetSelectedGeometry().SetShape(shape);
    }
}
