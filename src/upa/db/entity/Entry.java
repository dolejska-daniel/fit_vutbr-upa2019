package upa.db.entity;

import upa.Application;
import upa.db.exception.NotFoundException;
import upa.db.exception.QueryException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public Entry()
    {
    }

    public Entry(int id)
    {
        this.id = id;
    }

    @Override
    public void Create() throws QueryException
    {
        final String query = "INSERT INTO entries (id, name, description) VALUES (?, ?, ?);";
        try (PreparedStatement insertQuery = Application.connection.prepareStatement(query))
        {
            insertQuery.setInt(1, this.id);
            insertQuery.setString(2, this.name);
            insertQuery.setString(3, this.description);

            insertQuery.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Entry 'INSERT' query.", e);
        }
    }

    @Override
    public void Update() throws NotFoundException, QueryException
    {
        final String query = "UPDATE entries SET id=?, name=?, description=? WHERE id=?;";
        try (PreparedStatement updateQuery = Application.connection.prepareStatement(query))
        {
            updateQuery.setInt(1, this.id);
            updateQuery.setString(2, this.name);
            updateQuery.setString(3, this.description);
            updateQuery.setInt(4, this.id);

            if (updateQuery.executeUpdate() == 0)
                throw new NotFoundException(String.format("No rows were updated in database! No entry with ID '%d' has been found.", this.id));
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Entry 'UPDATE' query.", e);
        }
    }

    @Override
    public void Delete() throws NotFoundException, QueryException
    {
        final String query = "DELETE FROM entries WHERE id=?;";
        try (PreparedStatement deleteQuery = Application.connection.prepareStatement(query))
        {
            deleteQuery.setInt(1, this.id);

            if (deleteQuery.executeUpdate() == 0)
                throw new NotFoundException(String.format("No rows were deleted from database! No entry with ID '%d' has been found.", this.id));
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Entry 'DELETE' query.", e);
        }
    }
}
