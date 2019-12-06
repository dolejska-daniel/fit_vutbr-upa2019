package upa.db.entity;

import upa.Application;

import java.sql.Connection;

/**
 * Database entity base class.
 *
 * @author Daniel Dolejška
 * @since 2019-10-15
 */
public abstract class EntityBase
{
    //=====================================================================dd==
    // CONSTRUCTORS
    //=====================================================================dd==

    public EntityBase()
    {
    }


    //=====================================================================dd==
    // HELPER METHODS
    //=====================================================================dd==

    /**
     * Database connection getter.
     *
     * @return Database connection.
     */
    public static Connection GetConnection()
    {
        return Application.connection;
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
