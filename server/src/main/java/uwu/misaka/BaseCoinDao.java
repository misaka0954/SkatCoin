package uwu.misaka;

import java.sql.*;
import java.util.*;

public class BaseCoinDao extends AbstractDao<BaseCoin>{

    public BaseCoinDao(){
        try{
            preparedStatement("create table if not exists base_coin(\n" +
                    "\tid varchar(256)\n" +
                    "\t\tconstraint base_coin_pk\n" +
                    "\t\t\tprimary key,\n" +
                    "\twallet_id varchar(256) default 0\n" +
                    "\t\treferences wallet\n" +
                    ");").executeUpdate();
        }catch(Throwable t){
            throw new RuntimeException(t);
        }
    }

    @Override
    public List<BaseCoin> getAll(){
        List<BaseCoin> list = new ArrayList<>();
        try(PreparedStatement statement = preparedStatement("select * from base_coin;"); ResultSet set = statement.executeQuery()){
            while(set.next()){
                list.add(new BaseCoin(set.getString("id"), set.getString("wallet_id")));
            }
            return list;
        }catch(Throwable t){
            t.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public BaseCoin getById(String id){
        Objects.requireNonNull(id, "id");
        try(PreparedStatement statement = preparedStatement("select * from base_coin where id = ?;", id); ResultSet set = statement.executeQuery()){
            if(set.next()){
                return new BaseCoin(id, set.getString("wallet_id"));
            }
            return null;
        }catch(Throwable t){
            t.printStackTrace();
            return null;
        }
    }

    public List<String> getCoinsByWalletId(String walletId){
        Objects.requireNonNull(walletId, "id");
        ArrayList<String> rtnSet = new ArrayList<>();
        try(PreparedStatement statement = preparedStatement("select * from base_coin where wallet_id = ?;", walletId); ResultSet set = statement.executeQuery()){
            while(set.next()){
                 rtnSet.add(set.getString("id"));
            }
            return rtnSet;
        }catch(Throwable t){
            t.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(BaseCoin entity){
        Objects.requireNonNull(entity, "entity");
        try(PreparedStatement statement = preparedStatement("insert or replace into base_coin values (?, ?);", entity.id, entity.walletId)){
            statement.executeUpdate();
        }catch(Throwable t){
            t.printStackTrace();
        }
    }
}
