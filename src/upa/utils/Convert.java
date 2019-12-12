package upa.utils;

import oracle.spatial.geometry.JGeometry;
import upa.utils.exception.ConversionException;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Class providing useful methods for data conversions.
 *
 * @author Daniel Dolejška
 * @since 2019-12-05
 */
public class Convert
{
    /**
     * Převzato a upraveno.
     *
     * @link https://rychly-edu.gitlab.io/dbs/spatial/oracle-lab/
     * @author RNDr. Marek Rychlý, Ph.D.
     */
    public static Shape Point2dToShape(Point2D pt)
    {
        return new Rectangle2D.Double(pt.getX() - 0.5, pt.getY() - 0.5, 1, 1);
    }

    /**
     * Převzato a upraveno.
     *
     * @link https://rychly-edu.gitlab.io/dbs/spatial/oracle-lab/
     * @author RNDr. Marek Rychlý, Ph.D.
     */
    public static Shape JGeometryToShape(JGeometry geometry) throws ConversionException
    {
        // check a type of JGeometry object
        switch (geometry.getType())
        {
            // return a shape for non-points
            case JGeometry.GTYPE_CURVE:
            case JGeometry.GTYPE_POLYGON:
            case JGeometry.GTYPE_COLLECTION:
            case JGeometry.GTYPE_MULTICURVE:
            case JGeometry.GTYPE_MULTIPOLYGON:
                return geometry.createShape();

            // return a rectangular "point" for a point (centered over the point location with unit dimensions)
            case JGeometry.GTYPE_POINT:
                return Convert.Point2dToShape(geometry.getJavaPoint());

            // return an area of rectangular "points" for all points (each centered over the points location with unit dimensions)
            case JGeometry.GTYPE_MULTIPOINT:
                final Area area = new Area();
                for (Point2D pt : geometry.getJavaPoints())
                {
                    Shape s = Convert.Point2dToShape(pt);
                    area.add(new Area(s));
                }

                return area;

            // it is something else (we do not know how to convert)
            default:
                throw new ConversionException("Unable to convert provided JGeometry instance to Shape instance.");
        }
    }

    /**
     * Převzato a upraveno.
     *
     * @link https://rychly-edu.gitlab.io/dbs/spatial/oracle-lab/
     * @author RNDr. Marek Rychlý, Ph.D.
     */
    public static JGeometry DbDataToJGeometry(final byte[] data) throws ConversionException
    {
        try
        {
            JGeometry result = JGeometry.load(data);
            // System.out.println("geometry loaded: " + result.toStringFull());

            return result;
        }
        catch (Exception exception)
        {
            throw new ConversionException("Failed to convert database data to JGeometry object instance.", exception);
        }
    }

    public static JGeometry ShapeToJGeometry(Shape shape) throws ConversionException
    {
        // check a type of JGeometry object
        if (shape instanceof Rectangle)
        {
            Rectangle rec = (Rectangle) shape;
            Point pt = rec.getLocation();
            return JGeometry.createLinearPolygon(new double[]{
                    pt.getX(), pt.getY(),
                    pt.getX() + rec.getWidth(), pt.getY(),
                    pt.getX() + rec.getWidth(), pt.getY() + rec.getHeight(),
                    pt.getX(), pt.getY() + rec.getHeight(),
            }, 2, 0);
        }

        throw new ConversionException("Unable to convert provided Shape instance to JGeometry instance.");
    }
}

