package upa.db.entity;

import oracle.ord.im.OrdImage;
import upa.db.exception.GeneralDatabaseException;
import upa.db.exception.NotFoundException;
import upa.db.exception.QueryException;
import upa.utils.Query;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Image entry for building entry.
 *
 * @author Daniel Dolejška
 * @since 2019-12-04
 */
public class Image extends EntityBase
{
    /**
     * Unique image ID.
     */
    public int id;

    /**
     * Related building entry ID.
     */
    public int entry_id;

    /**
     * Image's title.
     */
    public String title;

    /**
     * Image's description.
     */
    public String description;

    /**
     * Image itself.
     */
    public OrdImage image;


    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    public Image()
    {
    }

    public Image(final int id)
    {
        this.id = id;
    }


    //=====================================================================dd==
    // HELPER METHODS
    //=====================================================================dd==

    /**
     * Loads image from file into this object instance.
     *
     * @param filepath Path to image file.
     * @throws GeneralDatabaseException Load failed.
     */
    public void LoadImage(final Path filepath) throws GeneralDatabaseException
    {
        try
        {
            this.image.loadDataFromFile(filepath.toString());
        }
        catch (Exception e)
        {
            throw new GeneralDatabaseException(String.format("Failed to load image from '%s'.", filepath), e);
        }
    }

    /**
     * Creates Image instance from data from database selection query.
     *
     * @param resultSet Successfully executed database selection query.
     * @return Image instance carrying data from database query.
     * @throws SQLException Query with invalid selection provided.
     */
    private static Image CreateFromResultSet(ResultSet resultSet) throws SQLException
    {
        Image image = new Image();
        image.id = resultSet.getInt(1);
        image.entry_id = resultSet.getInt(2);
        image.title = resultSet.getString(3);
        image.description = resultSet.getString(4);
        image.image = (OrdImage) Query.GetORAData(resultSet, 5, OrdImage.getORADataFactory());

        return image;
    }

    /**
     * Creates Image instance for each row from database selection query.
     *
     * @param resultSet Successfully executed database selection query.
     * @param arrayList Output list of image instances.
     * @throws SQLException Query with invalid selection provided.
     */
    private static void FillArrayFromResultSet(ResultSet resultSet, ArrayList<Image> arrayList) throws SQLException
    {
        do
        {
            Image image = Image.CreateFromResultSet(resultSet);
            arrayList.add(image);
        }
        while (resultSet.next());
    }


    //=====================================================================dd==
    // ENTITY DATABASE METHODS
    //=====================================================================dd==

    /**
     * Get image row with provided ID from database.
     *
     * @return Image with provided ID.
     * @throws QueryException Query execution failed.
     */
    public static Image Get(final int id) throws QueryException
    {
        // define SQL query
        final String query = "SELECT * FROM images WHERE id=?";
        try (PreparedStatement selectQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            selectQuery.setInt(1, id);

            // execute query
            ResultSet resultSet = selectQuery.executeQuery();
            if (resultSet == null || !resultSet.next())
                throw new NotFoundException(String.format("No rows were found in database! No image with ID '%d' has been found.", id));

            return Image.CreateFromResultSet(resultSet);
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Image 'GET' query.", e);
        }
    }

    /**
     * Loads all currently existing images from database.
     *
     * @return List of all existing images currently in database.
     * @throws QueryException Query execution failed.
     */
    public static List<Image> GetAll() throws QueryException
    {
        ArrayList<Image> array = new ArrayList<>();

        // define SQL query
        final String query = "SELECT * FROM images";
        try (PreparedStatement selectQuery = GetConnection().prepareStatement(query))
        {
            // execute query
            ResultSet resultSet = selectQuery.executeQuery();
            if (resultSet == null || !resultSet.next())
                return array;

            Image.FillArrayFromResultSet(resultSet, array);
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Image 'GET ALL' query.", e);
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
        final String query = "INSERT INTO images (entry_id, title, description, image) " +
                "VALUES (?, ?, ?, ordsys.ordimage.init()) RETURNING id INTO ?";
        try (PreparedStatement insertQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            insertQuery.setInt(1, this.entry_id);
            insertQuery.setString(2, this.title);
            insertQuery.setString(3, this.description);

            // register return parameter
            Query.RegisterReturnId(insertQuery, 4);
            // execute insertion
            insertQuery.executeUpdate();
            // TODO: Check whether insertion was successful?

            // get returned row identifier
            this.id = Query.GetReturnId(insertQuery);

            // image needs to be uploaded by update
            // if it is set, then execute update too
            if (this.image != null)
                this.Update();
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Image 'INSERT' query.", e);
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
        if (this.image == null)
            throw new QueryException("Image instance is not set, cannot execute update without it.");

        // define SQL query
        final String query = "UPDATE images SET entry_id=?, title=?, description=?, image=? WHERE id=?";
        try (PreparedStatement updateQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            updateQuery.setInt(1, this.entry_id);
            updateQuery.setString(2, this.title);
            updateQuery.setString(3, this.description);
            Query.SetORAData(updateQuery, 4, this.image);
            updateQuery.setInt(5, this.id);

            // execute and validate query
            if (updateQuery.executeUpdate() == 0)
                throw new NotFoundException(String.format("No rows were updated in database! No image with ID '%d' has been found.", this.id));
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Image 'UPDATE' query.", e);
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
        final String query = "DELETE FROM images WHERE id=?";
        try (PreparedStatement deleteQuery = GetConnection().prepareStatement(query))
        {
            // set query parameters
            deleteQuery.setInt(1, this.id);

            // execute and validate query
            if (deleteQuery.executeUpdate() == 0)
                throw new NotFoundException(String.format("No rows were deleted from database! No image with ID '%d' has been found.", this.id));
        }
        catch (SQLException e)
        {
            throw new QueryException("Failed to process Image 'DELETE' query.", e);
        }
    }
}
