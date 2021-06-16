package uwu.misaka;

import java.sql.*;
import java.util.*;

public abstract class AbstractDao<T>{

    protected PreparedStatement preparedStatement(String sql, Object... values) throws SQLException{
        PreparedStatement statement = DatabaseResources.getConnection().prepareStatement(sql);

        for(int i = 0; i < values.length; i++){
            statement.setObject(i + 1, values[i]);
        }
        return statement;
    }

    public List<T> getAll(){
        return Collections.emptyList(); //no-op
    }

    public T getById(String id){
        return null; //no-op
    }

    public void save(T entity){
        //no-op
    }
}
