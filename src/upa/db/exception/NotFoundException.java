package upa.db.exception;

/**
 * Exception raised in some cases when queries return no result.
 *
 * @author Daniel Dolejška
 * @since 2019-12-05
 */
public class NotFoundException extends GeneralDatabaseException
{
    public NotFoundException()
    {
        super();
    }

    public NotFoundException(String message)
    {
        super(message);
    }

    public NotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
