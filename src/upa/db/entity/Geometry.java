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
     * Geometry type.
     */
    public String type;

    /**
     * Geometry data.
     */
    public JGeometry data;


    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    public Geometry()
    {
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
        geometry.type = resultSet.getString(3);
        geometry.data = Convert.DbDataToJGeometry(resultSet.getBytes(4));

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
        final String query = "SELECT * FROM geometry WHERE id=?";
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
        ArrayList<Geometry> array = new ArrayList<>();

        // define SQL query
        final String query = "SELECT * FROM geometry";
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
        ArrayList<Geometry> array = new ArrayList<>();

        // define SQL query
        final String query = "SELECT * FROM geometry WHERE entry_id=?";
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
        final String query = "INSERT INTO geometry (entry_id, type, data) " +
                "VALUES (?, ?, ?) RETURNING id INTO ?";
        try (PreparedStatement insertQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            insertQuery.setInt(1, this.entry_id);
            insertQuery.setString(2, this.type);
            insertQuery.setObject(3, JGeometry.store(GetConnection(), this.data));

            // register return parameter
            Query.RegisterReturnId(insertQuery, 4);
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
        final String query = "UPDATE geometry SET entry_id=?, type=?, data=? WHERE id=?";
        try (PreparedStatement updateQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            updateQuery.setInt(1, this.entry_id);
            updateQuery.setString(2, this.type);
            updateQuery.setObject(3, JGeometry.store(this.data));
            updateQuery.setInt(4, this.id);

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
