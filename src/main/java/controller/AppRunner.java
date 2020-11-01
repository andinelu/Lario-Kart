/**
 * Runner object for the Lario Kart application.
 */
public class AppRunner{

    /**
     * Main starter method or entry point for the Java program.
     *
     * @param args Unused
     */
    public static void main(String[] args)
    {
        AppController baseApp = new AppController();
        baseApp.init(args);
    }
}
