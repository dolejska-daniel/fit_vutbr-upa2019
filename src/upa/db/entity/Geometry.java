package upa.db.entity;

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

    // TODO: Add field for geometry data

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
    // ENTITY DATABASE METHODS
    //=====================================================================dd==

    public static Geometry Get(final int id)
    {
        // TODO: Implement Get functionality
        return null;
    }

    public static List<Geometry> GetAll()
    {
        // TODO: Implement GetAll functionality
        return new ArrayList<>();
    }

    @Override
    public void Create()
    {
        // TODO: Implement Create functionality
    }

    @Override
    public void Update()
    {
        // TODO: Implement Update functionality
    }

    @Override
    public void Delete()
    {
        // TODO: Implement Delete functionality
    }
}
