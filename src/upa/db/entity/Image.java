package upa.db.entity;

import oracle.ord.im.OrdImage;

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

    public Image()
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
