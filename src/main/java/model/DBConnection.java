import com.mchange.v2.c3p0.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.net.URL;

/**
 * DBConnection class creates the connection pool for our game.
 */
public class DBConnection {
    private static DBConnection instance = null;
    private static ComboPooledDataSource cpds;

    private DBConnection() {
        init();
    }

    private void init() {
        URL file = getClass().getResource(AppController.PROPERTIES_URL);
        if (file == null){
            // Error
            System.out.println("File not found");
            return;
        }

        Properties properties = new Properties();
        InputStream in = getClass().getResourceAsStream(AppController.PROPERTIES_URL);

        try {
            properties.load(in);

            cpds = new ComboPooledDataSource();
            cpds.setDriverClass("com.mysql.cj.jdbc.Driver");
            cpds.setJdbcUrl(properties.getProperty("URL").trim());
            cpds.setUser(properties.getProperty("Username").trim());
            cpds.setPassword(properties.getProperty("Password").trim());
            cpds.setInitialPoolSize(1);
            cpds.setAcquireIncrement(1);
            cpds.setMinPoolSize(1);
            cpds.setMaxPoolSize(4);
            cpds.setMaxStatements(180);
        }
        catch (IOException e){

        }
        catch (PropertyVetoException e){
            System.out.println("Error :)");
            System.out.println(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DBConnection.getInstance().cpds.close();
            System.out.println("Closing connection pool");
        }, "Shutdown-thread"));
    }

    /**
     * getConnection is a method that fetches a connection from the connection pool
     * @return connection.
     */
    public static Connection getConnection() {

        Connection connection = null;
        try{
            connection = getInstance().cpds.getConnection();
        }
        catch(SQLException e){
            System.out.println(e);
            // Error
        }
        return connection;
    }

    private static DBConnection getInstance(){
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * close method closes the connection to the connection pool.
     */
    public static void close() {
        try {
            getConnection().close();
        } catch (SQLException e) {

        }
    }
}
