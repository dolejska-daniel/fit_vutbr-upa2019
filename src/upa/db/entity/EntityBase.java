package upa.db.entity;

import upa.db.Connection;

/**
 * Database entity base class.
 *
 * @author Daniel Dolejška
 * @since 2019-10-15
 */
public abstract class EntityBase
{
    /**
     * Database connection instance.
     */
    private Connection connection;

    public EntityBase()
    {
    }

    /**
     * Database connection setter.
     *
     * @param c Database connection.
     */
    private void setConnection(Connection c)
    {
        assert (c != null);
        this.connection = c;
    }

    /**
     * Database connection getter.
     *
     * @return Database connection.
     */
    public Connection getConnection()
    {
        return connection;
    }

    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    /**
     * Class constructor.
     *
     * @param c Database connection.
     */
    public EntityBase(Connection c)
    {
        this.setConnection(c);
    }

    //=====================================================================dd==
    // ENTITY DATABASE METHODS
    //=====================================================================dd==

    /**
     * Insert new record to database.
     */
    public abstract void Create();

    /**
     * Update corresponding record's values in database.
     */
    public abstract void Update();

    /**
     * Remove corresponding record from database.
     */
    public abstract void Delete();
}
