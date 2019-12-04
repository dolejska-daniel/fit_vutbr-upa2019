package db.entity;

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

    @Override
    public void Create()
    {
    }

    @Override
    public void Update()
    {
    }

    @Override
    public void Delete()
    {
    }
}
