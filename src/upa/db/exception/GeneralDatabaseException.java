package upa.db.exception;

/**
 * Base class representing any database related exceptions.
 *
 * @author Daniel Dolejška
 * @since 2019-10-15
 */
public class GeneralDatabaseException extends RuntimeException
{
    public GeneralDatabaseException()
    {
        super();
    }

    public GeneralDatabaseException(String message)
    {
        super(message);
    }

    public GeneralDatabaseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
