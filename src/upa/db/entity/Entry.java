package upa.db.entity;

import upa.db.exception.NotFoundException;
import upa.db.exception.QueryException;
import upa.utils.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Building entry.
 *
 * @author Daniel Dolejška
 * @since 2019-12-04
 */
public class Entry extends EntityBase
{
    /**
     * Unique building ID.
     */
    public int id;

    /**
     * Building's name.
     */
    public String name;

    /**
     * Building's description.
     */
    public String description;

    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    public Entry()
    {
    }

    public Entry(final int id)
    {
        this.id = id;
    }


    //=====================================================================dd==
    // HELPER METHODS
    //=====================================================================dd==

    /**
     * Creates Entry instance from data from database selection query.
     *
     * @param resultSet Successfully executed database selection query.
     * @return Entry instance carrying data from database query.
     * @throws SQLException Query with invalid selection provided.
     */
    private static Entry CreateFromResultSet(ResultSet resultSet) throws SQLException
    {
        Entry entry = new Entry();
        entry.id = resultSet.getInt(1);
        entry.name = resultSet.getString(2);
        entry.description = resultSet.getString(3);

        return entry;
    }

    /**
     * Creates Entry instance for each row from database selection query.
     *
     * @param resultSet Successfully executed database selection query.
     * @param arrayList Output list of image instances.
     * @throws SQLException Query with invalid selection provided.
     */
    private static void FillArrayFromResultSet(ResultSet resultSet, ArrayList<Entry> arrayList) throws SQLException
    {
        do
        {
            Entry entry = Entry.CreateFromResultSet(resultSet);
            arrayList.add(entry);
        }
        while (resultSet.next());
    }


    //=====================================================================dd==
    // ENTITY DATABASE METHODS
    //=====================================================================dd==

    /**
     * Get entry row with provided ID from database.
     *
     * @return Entry with provided ID.
     * @throws QueryException Query execution failed.
     */
    public static Entry Get(final int id)
    {
        // define SQL query
        final String query = "SELECT * FROM entries WHERE id=?";
        try (PreparedStatement selectQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            selectQuery.setInt(1, id);

            // execute query
            ResultSet resultSet = selectQuery.executeQuery();
            if (resultSet == null || !resultSet.next())
                throw new NotFoundException(String.format("No rows were found in database! No entry with ID '%d' has been found.", id));

            return Entry.CreateFromResultSet(resultSet);
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Entry 'GET' query.", e);
        }
    }

    /**
     * Loads all currently existing entries from database.
     *
     * @return List of all existing entries currently in database.
     * @throws QueryException Query execution failed.
     */
    public static List<Entry> GetAll()
    {
        ArrayList<Entry> array = new ArrayList<>();

        // define SQL query
        final String query = "SELECT * FROM entries";
        try (PreparedStatement selectQuery = GetConnection().prepareStatement(query))
        {
            // execute query
            ResultSet resultSet = selectQuery.executeQuery();
            if (resultSet == null || !resultSet.next())
                return array;

            Entry.FillArrayFromResultSet(resultSet, array);
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Entry 'GET ALL' query.", e);
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
        // define SQL query
        final String query = "INSERT INTO entries (name, description) " +
                "VALUES (?, ?) RETURNING id INTO ?";
        try (PreparedStatement insertQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            insertQuery.setInt(1, this.id);
            insertQuery.setString(2, this.name);
            insertQuery.setString(3, this.description);

            // register return parameter
            Query.RegisterReturnId(insertQuery, 4);
            // execute insertion
            insertQuery.executeUpdate();
            // TODO: Check whether insertion was successful?

            // get returned row identifier
            this.id = Query.GetReturnId(insertQuery);
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Entry 'INSERT' query.", e);
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
        // define SQL query
        final String query = "UPDATE entries SET id=?, name=?, description=? WHERE id=?";
        try (PreparedStatement updateQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            updateQuery.setInt(1, this.id);
            updateQuery.setString(2, this.name);
            updateQuery.setString(3, this.description);
            updateQuery.setInt(4, this.id);

            // execute and validate query
            if (updateQuery.executeUpdate() == 0)
                throw new NotFoundException(String.format("No rows were updated in database! No entry with ID '%d' has been found.", this.id));
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Entry 'UPDATE' query.", e);
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
        final String query = "DELETE FROM entries WHERE id=?";
        try (PreparedStatement deleteQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            deleteQuery.setInt(1, this.id);

            // execute and validate query
            if (deleteQuery.executeUpdate() == 0)
                throw new NotFoundException(String.format("No rows were deleted from database! No entry with ID '%d' has been found.", this.id));
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Entry 'DELETE' query.", e);
        }
    }
}
