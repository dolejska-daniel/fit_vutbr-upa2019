package upa.db;

import oracle.jdbc.pool.OracleDataSource;
import upa.gui.WindowManager;

import java.io.File;
import java.io.FileInputStream;
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
        // create configuration file instance
        final File file = new File("config/database.txt");
        // validate that the file exists
        if (!file.exists())
        {
            WindowManager.ShowErrorMessageDialog(String.format("Database configuration file does not exist in '%s'.\n\nProgram will now exit.", file.toPath().toAbsolutePath()), "Database configuration not found");
            System.exit(1);
        }

        // try to load configuration from this file
        try
        {
            FileInputStream config = new FileInputStream(file);
            props.load(config);
        }
        catch (Exception e)
        {
            String message = String.format(
                    "Failed to load database configuration from file '%s'!\nEncountered exception %s: %s\n\nProgram will now exit.",
                    file.toPath().toAbsolutePath(), e.getClass(), e.getMessage()
            );
            WindowManager.ShowErrorMessageDialog(message, "Database configuration failed to load");
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
