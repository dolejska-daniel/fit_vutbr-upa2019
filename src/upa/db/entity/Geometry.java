package upa.db.entity;

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

    // TODO: Add field for geometry data

    public Geometry()
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
