package upa.db.exception;

/**
 * Exception class representing further unspecified database query exceptions.
 *
 * @author Daniel Dolejška
 * @since 2019-12-05
 */
public class QueryException extends GeneralDatabaseException
{
    public QueryException()
    {
        super();
    }

    public QueryException(String message)
    {
        super(message);
    }

    public QueryException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
