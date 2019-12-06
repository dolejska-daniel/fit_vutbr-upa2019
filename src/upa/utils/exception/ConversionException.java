package upa.utils.exception;

/**
 * Class representing exception during data conversion.
 *
 * @author Daniel Dolejška
 * @since 2019-12-05
 */
public class ConversionException extends Exception
{
    public ConversionException()
    {
        super();
    }

    public ConversionException(String message)
    {
        super(message);
    }

    public ConversionException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
