package upa.utils;

import oracle.spatial.geometry.JGeometry;
import upa.utils.exception.ConversionException;

import java.awt.*;
import java.awt.geom.*;

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
                Shape shape = geometry.createShape();

                if (shape instanceof GeneralPath)
                {
                    shape = Convert.GeneralPathToPolygon((GeneralPath) shape);
                }

                return shape;

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
        if (shape instanceof Rectangle2D)
        {
            Rectangle2D rec = (Rectangle2D) shape;
            return new JGeometry(rec.getX(), rec.getY(), rec.getX() + rec.getWidth(), rec.getY() + rec.getHeight(), 0);
        }
        else if (shape instanceof Ellipse2D)
        {
            Ellipse2D ellipse = (Ellipse2D) shape;

            final Point pt1 = ellipse.getBounds().getLocation();
            pt1.x += (ellipse.getBounds().getMaxX() - pt1.x) / 2;

            final double r = pt1.distance(ellipse.getCenterX(), ellipse.getCenterY());
            return JGeometry.createCircle(ellipse.getCenterX(), ellipse.getCenterY(), r, 2);
        }
        else if (shape instanceof Polygon)
        {
            Polygon polygon = (Polygon) shape;
            final double[] doubles = new double[polygon.npoints * 2];
            for (int i = 0; i < polygon.npoints; i++)
            {
                doubles[i * 2] = polygon.xpoints[i];
                doubles[i * 2 + 1] = polygon.ypoints[i];
            }
            return JGeometry.createLinearPolygon(doubles, 2, 0);
        }
        else if (shape instanceof GeneralPath)
        {
            GeneralPath path = (GeneralPath) shape;

        }

        throw new ConversionException(String.format("Unable to convert provided Shape instance (%s) to JGeometry instance.", shape.getClass()));
    }

    public static String ShapeToInternalType(Shape shape) throws ConversionException
    {
        if (shape instanceof Rectangle2D)
        {
            return "Rectangle";
        }
        else if (shape instanceof Ellipse2D)
        {
            return "Ellipse";
        }
        else if (shape instanceof Polygon)
        {
            return "Polygon";
        }

        throw new ConversionException(String.format("Unable to convert provided Shape instance (%s) to corresponding internal type.", shape.getClass()));
    }

    public static Polygon GeneralPathToPolygon(GeneralPath path) throws ConversionException
    {
        final Polygon polygon = new Polygon();

        float[] coords = new float[6];
        PathIterator pathIterator = path.getPathIterator(new AffineTransform());
        while (!pathIterator.isDone())
        {
            switch (pathIterator.currentSegment(coords))
            {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    polygon.addPoint((int) coords[0], (int) coords[1]);
                    break;

                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    throw new ConversionException("Unable to convert provided GeneralPath instance to corresponding Polygon instance. The path is too difficult.");
            }
            pathIterator.next();
        }

        return polygon;
    }
}

