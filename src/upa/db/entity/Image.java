package upa.db.entity;

import oracle.ord.im.OrdImage;

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
    // ENTITY DATABASE METHODS
    //=====================================================================dd==

    public static Image Get(final int id)
    {
        // TODO: Implement Get functionality
        return null;
    }

    public static List<Image> GetAll()
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
