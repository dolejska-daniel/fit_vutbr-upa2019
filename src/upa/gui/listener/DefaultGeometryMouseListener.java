package upa.gui.listener;

import upa.gui.MainWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class DefaultGeometryMouseListener extends MouseAdapter
{
    private MainWindow mainWindow;
    private Point sourceLocation;
    private int sourceDistX;
    private int sourceDistY;
    private int prevDistDiffX;
    private int prevDistDiffY;

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
        sourceDistX = e.getX();
        sourceDistY = e.getY();

        prevDistDiffX = 0;
        prevDistDiffY = 0;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (!HasSelectedGeometry())
            return;

        int distDiffX = sourceDistX - e.getX();
        int distDiffY = sourceDistY - e.getY();

        final Shape shape = GetSelectedGeometry();

        if (shape instanceof Rectangle2D)
        {
            Rectangle2D rec = (Rectangle2D) shape;
            final Rectangle2D.Double newRec = new Rectangle2D.Double(
                    sourceLocation.x - distDiffX, sourceLocation.y - distDiffY,
                    rec.getWidth(), rec.getHeight()
            );
            OverwriteSelectedGeometryShape(newRec);
        }
        else if (shape instanceof Ellipse2D)
        {
            Ellipse2D ellipse = (Ellipse2D) shape;
            final Ellipse2D.Double newEllipse = new Ellipse2D.Double(
                    sourceLocation.x - distDiffX, sourceLocation.y - distDiffY,
                    ellipse.getWidth(), ellipse.getHeight()
            );
            OverwriteSelectedGeometryShape(newEllipse);
        }
        else if (shape instanceof Polygon)
        {
            Polygon polygon = (Polygon) shape;
            final Point currentLocation = shape.getBounds().getLocation();

            final int[] xpoints = polygon.xpoints;
            for (int i = 0; i < xpoints.length; i++)
                xpoints[i] = xpoints[i] + (prevDistDiffX - distDiffX);

            final int[] ypoints = polygon.ypoints;
            for (int i = 0; i < ypoints.length; i++)
                ypoints[i] = ypoints[i] + (prevDistDiffY - distDiffY);

            final Polygon newPolygon = new Polygon(xpoints, ypoints, polygon.npoints);
            OverwriteSelectedGeometryShape(newPolygon);

            prevDistDiffX = distDiffX;
            prevDistDiffY = distDiffY;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (!HasSelectedGeometry())
            return;

        SaveShapeChanges();
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        if (!HasSelectedGeometry())
            return;

        SaveShapeChanges();
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
        mainWindow.ReloadSelectedGeometry();
        mainWindow.repaint();
    }

    private void SaveShapeChanges()
    {
        mainWindow.GetSelectedGeometry().Update();
    }
}
