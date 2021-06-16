package uwu.misaka;

import java.sql.*;
import java.util.*;

public class WalletDao extends AbstractDao<Wallet>{

    public WalletDao(){
        try{
            preparedStatement("create table if not exists wallet(" +
                    "\tid varchar(256)\n" +
                    "\t\tconstraint wallet_pk\n" +
                    "\t\t\tprimary key,\n" +
                    "\tlogin varchar(256),\n" +
                    "\tpassword varchar(256)\n" +
                    ");").executeUpdate();
        }catch(Throwable t){
            throw new RuntimeException(t);
        }
    }

    @Override
    public List<Wallet> getAll(){
        List<Wallet> list = new ArrayList<>();
        try(PreparedStatement statement = preparedStatement("select * from wallet;"); ResultSet set = statement.executeQuery()){
            while(set.next()){
                list.add(new Wallet(set.getString("id"), set.getString("login"), set.getString("password")));
            }
            return list;
        }catch(Throwable t){
            t.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Wallet getById(String id){
        Objects.requireNonNull(id, "id");
        try(PreparedStatement statement = preparedStatement("select * from wallet where id = ?;", id); ResultSet set = statement.executeQuery()){
            if(set.next()){
                return new Wallet(id, set.getString("login"), set.getString("password"));
            }
            return null;
        }catch(Throwable t){
            t.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Wallet entity){
        Objects.requireNonNull(entity, "entity");
        try(PreparedStatement statement = preparedStatement("insert or replace into wallet values (?, ?, ?);", entity.id, entity.login, entity.password)){
            statement.executeUpdate();
        }catch(Throwable t){
            t.printStackTrace();
        }
    }
}
