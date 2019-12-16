package upa.db.entity;

import oracle.spatial.geometry.JGeometry;
import upa.db.exception.NotFoundException;
import upa.db.exception.QueryException;
import upa.utils.Convert;
import upa.utils.Query;
import upa.utils.exception.ConversionException;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Geometry entry for building entry.
 *
 * @author Daniel Dolejška
 * @since 2019-12-04
 */
public class Geometry extends EntityBase
{
    /**
     * Unique geometry ID.
     */
    public int id;

    /**
     * Related building entry ID.
     */
    public int entry_id;

    /**
     * Layer identifier.
     */
    public int layer;

    /**
     * Geometry type.
     */
    public String type;

    /**
     * Geometry object type.
     */
    public String internal_type;

    /**
     * Geometry data.
     */
    public JGeometry data;


    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    public Geometry()
    {
        this.layer = GetNextLayerId();
    }

    public Geometry(final int id)
    {
        this.id = id;
    }


    //=====================================================================dd==
    // HELPER METHODS
    //=====================================================================dd==

    private Shape convertedData;

    public Shape Shape()
    {
        try
        {
            if (convertedData == null)
                convertedData = Convert.JGeometryToShape(this.data);
        }
        catch (ConversionException e)
        {
            e.printStackTrace();
        }

        return convertedData;
    }

    public void SetShape(Shape shape)
    {
        try
        {
            data = Convert.ShapeToJGeometry(shape);
            convertedData = shape;
        }
        catch (ConversionException e)
        {
            e.printStackTrace();
        }
    }

    private static int highestLayerId = 0;

    private static int GetNextLayerId()
    {
        return highestLayerId++;
    }

    /**
     * Creates Geometry instance from data from database selection query.
     *
     * @param resultSet Successfully executed database selection query.
     * @return Geometry instance carrying data from database query.
     * @throws SQLException Query with invalid selection provided.
     */
    private static Geometry CreateFromResultSet(ResultSet resultSet) throws SQLException, ConversionException
    {
        Geometry geometry = new Geometry();
        geometry.id = resultSet.getInt(1);
        geometry.entry_id = resultSet.getInt(2);
        geometry.layer = resultSet.getInt(3);
        geometry.type = resultSet.getString(4);
        geometry.internal_type = resultSet.getString(5);
        geometry.data = Convert.DbDataToJGeometry(resultSet.getBytes(6));

        return geometry;
    }

    /**
     * Creates Geometry instance for each row from database selection query.
     *
     * @param resultSet Successfully executed database selection query.
     * @param arrayList Output list of geometry instances.
     * @throws SQLException        Query with invalid selection provided.
     * @throws ConversionException Query results failed to be properly converted.
     */
    private static void FillArrayFromResultSet(ResultSet resultSet, ArrayList<Geometry> arrayList)
            throws SQLException, ConversionException
    {
        do
        {
            Geometry geometry = Geometry.CreateFromResultSet(resultSet);
            arrayList.add(geometry);
        }
        while (resultSet.next());
    }


    //=====================================================================dd==
    // ENTITY DATABASE METHODS
    //=====================================================================dd==

    /**
     * Get geometry row with provided ID from database.
     *
     * @return Geometry with provided ID.
     * @throws QueryException Query execution failed.
     */
    public static Geometry Get(final int id)
    {
        // define SQL query
        final String query = "SELECT * FROM geometry WHERE id=? ORDER BY layer";
        try (PreparedStatement selectQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            selectQuery.setInt(1, id);

            // execute query
            ResultSet resultSet = selectQuery.executeQuery();
            if (resultSet == null || !resultSet.next())
                throw new NotFoundException(String.format("No rows were found in database! No entry with ID '%d' has been found.", id));

            return Geometry.CreateFromResultSet(resultSet);
        }
        catch (Exception e)
        {
            throw new QueryException("Failed to process Geometry 'GET' query.", e);
        }
    }

    /**
     * Loads all currently existing geometry entries from database.
     *
     * @return List of all existing geometry entries currently in database.
     * @throws QueryException Query execution failed.
     */
    public static List<Geometry> GetAll() throws QueryException
    {
        highestLayerId = 0;
        ArrayList<Geometry> array = new ArrayList<>();

        // define SQL query
        final String query = "SELECT * FROM geometry ORDER BY entry_id, layer";
        try (PreparedStatement selectQuery = GetConnection().prepareStatement(query))
        {
            // execute query
            ResultSet resultSet = selectQuery.executeQuery();
            if (resultSet == null || !resultSet.next())
                return array;

            Geometry.FillArrayFromResultSet(resultSet, array);
        }
        catch (Exception e)
        {
            throw new QueryException("Failed to process Geometry 'GET ALL' query.", e);
        }

        return array;
    }

    /**
     * Loads all currently existing entries for given entry_id from database.
     *
     * @return List of all existing entries for given entry_id currently in database.
     * @throws QueryException Query execution failed.
     */
    public static List<Geometry> GetAll(final int entry_id) throws QueryException
    {
        highestLayerId = 0;
        ArrayList<Geometry> array = new ArrayList<>();

        // define SQL query
        final String query = "SELECT * FROM geometry WHERE entry_id=? ORDER BY layer";
        try (PreparedStatement selectQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            selectQuery.setInt(1, entry_id);

            // execute query
            ResultSet resultSet = selectQuery.executeQuery();
            if (resultSet == null || !resultSet.next())
                return array;

            Geometry.FillArrayFromResultSet(resultSet, array);
        }
        catch (Exception e)
        {
            throw new QueryException("Failed to process Geometry 'GET ALL' query.", e);
        }

        return array;
    }

    public static double GetArea(final int entry_id, final String type)
    {
        // define SQL query
        final String query = "SELECT SDO_GEOM.SDO_AREA(total_floor, 0.1) " +
                "FROM (SELECT SDO_AGGR_UNION(SDOAGGRTYPE(g.data, 0.1)) total_floor FROM geometry g WHERE g.entry_id=? AND g.type LIKE ?)";
        try (PreparedStatement insertQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            insertQuery.setInt(1, entry_id);
            insertQuery.setString(2, type);

            // execute insertion
            ResultSet result = insertQuery.executeQuery();

            result.next();
            return result.getDouble(1);
        }
        catch (Exception e)
        {
            throw new QueryException("Failed to process Geometry 'GET AREA' query.", e);
        }
    }

    public static double GetClearArea(final int entry_id, final String type)
    {
        // define SQL query
        final String query = "SELECT SDO_GEOM.SDO_AREA(total_floor, 0.1) total_floor_area, SDO_GEOM.SDO_AREA(SDO_GEOM.SDO_INTERSECTION(total_floor, total_other, 0.1)) total_intersecting_area " +
                "FROM (SELECT SDO_AGGR_UNION(SDOAGGRTYPE(g.data, 0.1)) total_floor FROM geometry g WHERE g.entry_id=? AND g.type LIKE ?)," +
                "(SELECT SDO_AGGR_UNION(SDOAGGRTYPE(g.data, 0.1)) total_other FROM geometry g WHERE g.entry_id=? AND g.type NOT LIKE ?)";
        try (PreparedStatement insertQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            insertQuery.setInt(1, entry_id);
            insertQuery.setString(2, type);
            insertQuery.setInt(3, entry_id);
            insertQuery.setString(4, type);

            // execute insertion
            ResultSet result = insertQuery.executeQuery();

            result.next();
            return result.getDouble(1) - result.getDouble(2);
        }
        catch (Exception e)
        {
            throw new QueryException("Failed to process Geometry 'GET CLEAR AREA' query.", e);
        }
    }

    /**
     * Inserts new row with this object's data into database.
     *
     * @throws QueryException Query execution failed.
     */
    @Override
    public void Create() throws QueryException
    {
        if (this.data == null)
            throw new QueryException("Geometry instance is not set, cannot execute insertion without it.");

        // define SQL query
        final String query = "INSERT INTO geometry (entry_id, layer, type, internal_type, data) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id INTO ?";
        try (PreparedStatement insertQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            insertQuery.setInt(1, this.entry_id);
            insertQuery.setInt(2, this.layer);
            insertQuery.setString(3, this.type);
            insertQuery.setString(4, this.internal_type);
            insertQuery.setObject(5, JGeometry.store(GetConnection(), this.data));

            // register return parameter
            Query.RegisterReturnId(insertQuery, 6);
            // execute insertion
            insertQuery.executeUpdate();
            // TODO: Check whether insertion was successful?

            // get returned row identifier
            this.id = Query.GetReturnId(insertQuery);
        }
        catch (Exception e)
        {
            throw new QueryException("Failed to process Geometry 'INSERT' query.", e);
        }
    }

    /**
     * Updates row with this object's ID with this object's data in database.
     *
     * @throws NotFoundException No rows were updated in database.
     * @throws QueryException    Query execution failed.
     */
    @Override
    public void Update() throws NotFoundException, QueryException
    {
        if (this.data == null)
            throw new QueryException("Geometry instance is not set, cannot execute update without it.");

        // define SQL query
        final String query = "UPDATE geometry SET entry_id=?, layer=?, type=?, internal_type=?, data=? WHERE id=?";
        try (PreparedStatement updateQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            updateQuery.setInt(1, this.entry_id);
            updateQuery.setInt(2, this.layer);
            updateQuery.setString(3, this.type);
            updateQuery.setString(4, this.internal_type);
            updateQuery.setObject(5, JGeometry.store(GetConnection(), this.data));
            updateQuery.setInt(6, this.id);

            // execute and validate query
            if (updateQuery.executeUpdate() == 0)
                throw new NotFoundException(String.format("No rows were updated in database! No geometry with ID '%d' has been found.", this.id));
        }
        catch (Exception e)
        {
            throw new QueryException("Failed to process Geometry 'UPDATE' query.", e);
        }
    }

    /**
     * Removes row with this object's ID from database.
     *
     * @throws NotFoundException No rows were removed from database.
     * @throws QueryException    Query execution failed.
     */
    @Override
    public void Delete() throws NotFoundException, QueryException
    {
        // define SQL query
        final String query = "DELETE FROM geometry WHERE id=?";
        try (PreparedStatement deleteQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            deleteQuery.setInt(1, this.id);

            // execute and validate query
            if (deleteQuery.executeUpdate() == 0)
                throw new NotFoundException(String.format("No rows were deleted from database! No geometry with ID '%d' has been found.", this.id));
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Geometry 'DELETE' query.", e);
        }
    }
}
