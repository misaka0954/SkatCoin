package uwu.misaka;

import org.sqlite.JDBC;

import java.sql.*;

public class DatabaseResources{

    static{
        Class<JDBC> clazz = JDBC.class; // принудительная статическая инициализация
    }

    private static volatile Connection connection;

    public static Connection getConnection(){
        if(connection == null){
            synchronized(DatabaseResources.class){
                if(connection == null){
                    connection = getConnection0();
                }
            }
        }

        try{
            if(connection.isClosed()){ // reconnect
                connection = getConnection0();
            }
        }catch(Throwable t){
            throw new RuntimeException(t);
        }


        return connection;
    }

    private static Connection getConnection0(){
        try{
            return DriverManager.getConnection("jdbc:sqlite:theskats.db");
        }catch(Throwable t){
            throw new RuntimeException(t);
        }
    }
}
