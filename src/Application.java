import gui.WindowManager;

/**
 * @author Daniel Dolejška
 * @since 2019-10-15
 */
public final class Application
{
    /**
     * @param args Application arguments
     */
    public static void main(String[] args)
    {
        WindowManager.Setup();
        WindowManager.Run();
    }
}
