package upa;

import upa.db.Connection;
import upa.gui.WindowManager;

/**
 * @author Daniel Dolejška
 * @since 2019-10-15
 */
public final class Application
{
    public static java.sql.Connection connection;

    /**
     * @param args upa.Application arguments
     */
    public static void main(String[] args)
    {
        Application.connection = Connection.CreateConnection();

        WindowManager.Setup();
        WindowManager.Run();
    }
}
