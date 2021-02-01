package Data;

import Business.Prateleira;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrateleirasDAO implements Map<String, Prateleira> {
    private static PrateleirasDAO singleton = null;

    private PrateleirasDAO() {
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS Prateleiras (" +
                    "CodPrateleira varchar(10) NOT NULL PRIMARY KEY," +
                    "Altura int(4) NOT NULL DEFAULT 0," +
                    "Estado BOOLEAN NOT NULL)" ;
            stm.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * Implementação do padrão Singleton
     * @return instância única desta classe
     */
    public static PrateleirasDAO getInstance() {
        if (PrateleirasDAO.singleton==null) {
            PrateleirasDAO.singleton = new PrateleirasDAO();
        }
        return PrateleirasDAO.singleton;
    }

    /**
     * @return número de prateleiras na base de dados
     * @throws NullPointerException em caso de erro
     */
    @Override
    public int size() throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        int i = 0;
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT count(*) FROM Prateleiras") ;
            if(rs.next()) {
                i = rs.getInt(1);
            }
        }
        catch (Exception e) {
            // Erro a criar tabela...
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }  finally {
            ConfigDAO.close(conn);
        }
        return i;
    }

    /**
     * Verifica se existem prateleiras na base de dados
     * @return true se existirem 0 prateleiras
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Certifica se um código de prateleira já existe na base de dados
     * @param key código da prateleira
     * @return true se código já existe
     * @throws NullPointerException em caso de erro
     */
    @Override
    public boolean containsKey(Object key) throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        boolean r;
        try {
            Statement stm = conn.createStatement();
            ResultSet rs =
                    stm.executeQuery("SELECT CodPrateleira FROM Prateleiras WHERE CodPrateleira='"+key.toString()+"'");
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    /**
     * Verifica se uma prateleira existe na base de dados
     * @param value prateleira
     * @return true caso a prateleira já exista
     */
    @Override
    public boolean containsValue(Object value) {
        Prateleira ptl = (Prateleira) value;
        boolean r = false;
        Prateleira c = this.get(ptl.getCodigo());
        if (c!=null){
            if(ptl.getDisponivel() == c.getDisponivel())
                r = ptl.getAltura() == c.getAltura();
        }
        return r;
    }

    /**
     * Obtém uma prateleira, dado o seu código
     * @param key código da prateleira
     * @return prateleira, caso exista, senão null
     * @throws NullPointerException em caso de erro
     */
    @Override
    public Prateleira get(Object key) throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        Prateleira ptl = null;
        try  {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Prateleiras p WHERE CodPrateleira='"+key.toString()+"' ");
            if (rs.next()) {  // A chave existe na tabela
                ptl = new Prateleira(rs.getString("CodPrateleira"), rs.getInt("Altura"),  rs.getBoolean("Estado"));
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
        return ptl;
    }

    /**
     * Insere uma prateleira na base de dados
     * @param key código da prateleira
     * @param value prateleira
     * @return prateleira adicionada
     * @throws NullPointerException em caso de erro
     */
    @Override
    public Prateleira put(String key, Prateleira value) throws NullPointerException{
        Prateleira ptl = null;
        String sql;
        int estado;
        if(value.getDisponivel()) estado = 1;
        else estado=0;
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            if (this.containsKey(key)) {
                stm.executeUpdate("UPDATE Prateleiras SET " +
                        "Altura = '" + value.getAltura() +
                        "', Estado = '" + estado +
                        "'WHERE CodPrateleira = '" + key + "'");
                return value;
            }
            else {
                sql =
                        "INSERT INTO Prateleiras (CodPrateleira, Altura, Estado) VALUES" +
                                " ('" + key + "'," +
                                "'" + value.getAltura() +
                                "','" + estado + "')";
            }
            int i = stm.executeUpdate(sql);
            return new Prateleira(value.getCodigo(), value.getAltura(), value.getDisponivel());
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * Remove uma prateleira da base de dados, dado o seu código
     * @param key código da prateleira a remover
     * @return prateleira removida
     * @throws NullPointerException em caso de erro
     */
    @Override
    public Prateleira remove(Object key) throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        try {
            Prateleira p = this.get(key);
            Statement stm = conn.createStatement();
            String sql = "DELETE FROM Prateleiras WHERE (`CodPrateleira` = " +
                    "'" + key + "');";
            int i = stm.executeUpdate(sql);
            return p;
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * Adiciona um conjunto de prateleiras à base de dados
     * @param m prateleiras a adicionar
     */
    @Override
    public void putAll(Map<? extends String, ? extends Prateleira> m) {
        throw new NullPointerException("Not implemented!");
    }

    /**
     * Apaga todas as prateleiras da base de dados
     */
    @Override
    public void clear() {
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate("DELETE FROM Prateleiras");
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * @return Set de códigos das prateleiras existentes na base de dados
     */
    @Override
    public Set<String> keySet() {
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("select distinct CodPrateleira from" +
                    " Prateleiras");
            Set<String> res = new HashSet<>();
            while (rs.next())
                res.add(rs.getString(1));
            return res;
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * @return todas as prateleiras da base de dados
     */
    @Override
    public Collection<Prateleira> values() {
        Collection<Prateleira> col = new HashSet<>();
        Set<String> keys = this.keySet();
        keys.forEach(x -> col.add(this.get(x)));
        return col;
    }

    @Override
    public Set<Entry<String, Prateleira>> entrySet() {
        throw new NullPointerException("Not implemented!");
    }

    /**
     * Verifica se existe alguma prateleira livre que tenha uma determinada altura
     * @param altura altura pretendida
     * @return código da prateleira disponível, caso exista, senão null
     */
    public String prateleiraLivre(float altura){
        Connection conn = ConfigDAO.connect();
        String codigo = null;
        try  {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT p.CodPrateleira, min(p.Altura) FROM Prateleiras p WHERE p.Estado=1 AND p.Altura>='"+altura+"' ");
            if (rs.next()) {  // A chave existe na tabela
                codigo = rs.getString("CodPrateleira");
            }
            return codigo;
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }
}
