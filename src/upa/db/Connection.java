package upa.db;

import oracle.jdbc.pool.OracleDataSource;

import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection class.
 *
 * @author Daniel Dolejška
 * @since 2019-10-15
 */
public class Connection
{
    /**
     * Create connection to database.
     *
     * @return Database connection instance or `null`.
     */
    public static java.sql.Connection CreateConnection()
    {
        // create connection properties
        Properties props = new Properties();
        // get current working directory and find configuration file
        Path config_path = FileSystems.getDefault().getPath("config/database.txt").toAbsolutePath();
        // try to load configuration from this file
        try
        {
            FileInputStream config = new FileInputStream(config_path.toString());
            props.load(config);
        }
        catch (Exception e)
        {
            String message = String.format(
                    "Failed to load database configuration from file '%s'!\nEncountered exception %s: '%s'",
                    config_path, e.getClass(), e.getMessage()
            );
            System.err.println(message);
            System.exit(1);
        }

        // try to initiate connection to the database
        // using provided configuration from file
        try
        {
            OracleDataSource dataSource = new OracleDataSource();
            dataSource.setURL(props.getProperty("connection_url"));
            dataSource.setConnectionProperties(props);

            return dataSource.getConnection();
        }
        catch (Exception e)
        {
            System.err.println("Encountered exception when initializing connection to database: " + e.getMessage());
            System.exit(1);
        }

        return null;
    }

    public static void CloseConnection(java.sql.Connection connection)
    {
        try
        {
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
