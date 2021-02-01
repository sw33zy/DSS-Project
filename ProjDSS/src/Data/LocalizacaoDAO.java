package Data;

import Business.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class LocalizacaoDAO implements Map<Integer, Localizacao> {
    private static LocalizacaoDAO singleton = null;
    private int n = 1;

    private LocalizacaoDAO() {
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS Prateleiras (" +
                    "CodPrateleira varchar(10) NOT NULL PRIMARY KEY," +
                    "Altura int(4) NOT NULL DEFAULT 0," +
                    "Estado BOOLEAN NOT NULL)" ;
            stm.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS Localizacao (" +
                    "IDvertice int(4) NOT NULL PRIMARY KEY," +
                    "Tipo BOOLEAN NOT NULL," +
                    "CodPrateleira varchar(10) NULL DEFAULT NULL," +
                    "FOREIGN KEY(CodPrateleira) REFERENCES Prateleiras(CodPrateleira))" ;
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
    public static LocalizacaoDAO getInstance() {
        if (LocalizacaoDAO.singleton==null) {
            LocalizacaoDAO.singleton = new LocalizacaoDAO();
        }
        return LocalizacaoDAO.singleton;
    }

    /**
     * @return número de localizações na base de dados
     * @throws NullPointerException em caso de erro
     */
    @Override
    public int size() throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        int i = 0;
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT count(*) FROM Localizacao") ;
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
     * Verifica se existem localizações na base de dados
     * @return true se existirem 0 localizações
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Certifica se um id de localização existe na base de dados
     * @param key id da localização
     * @return true se a localização existe
     * @throws NullPointerException em caso de erro
     */
    @Override
    public boolean containsKey(Object key) throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        boolean r;
        try {
            Statement stm = conn.createStatement();
            ResultSet rs =
                    stm.executeQuery("SELECT IDvertice FROM Localizacao WHERE IDvertice='"+key.toString()+"'");
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new NullPointerException("Not implemented!");
    }

    /**
     * Obtém uma localização dado o seu id
     * @param key id da localização
     * @return localização, caso exista, senão null
     * @throws NullPointerException em caso de erro
     */
    @Override
    public  Localizacao get(Object key) throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        int keyT = (Integer) key*10;
        int keyF = keyT +9;
        Map<String,Prateleira> prateleira = new HashMap<>();
        Localizacao l = null;
        try {
            Statement stm = conn.createStatement();
            if (stm.executeQuery("SELECT * FROM Localizacao l " +
                    "INNER JOIN Prateleiras p  on l.CodPrateleira = p.CodPrateleira " +
                    "WHERE l.IDvertice between '" + keyT + "' and '" + keyF + "'").next()){
                ResultSet rs = stm.executeQuery("SELECT * FROM Localizacao l " +
                        "INNER JOIN Prateleiras p  on l.CodPrateleira = p.CodPrateleira " +
                        "WHERE l.IDvertice between '" + keyT + "' and '" + keyF + "'");
                while (rs.next()) {
                        prateleira.put(rs.getString("CodPrateleira"),
                                new Prateleira(rs.getString("CodPrateleira"),
                                        rs.getInt("Altura"),
                                        rs.getBoolean("Estado")));
                }
                return new CorredorComArmazenamento((Integer) key, prateleira);
            }
            else{
                ResultSet rs = stm.executeQuery("SELECT * FROM Localizacao l " +
                        "WHERE l.IDvertice='" + (Integer)key + "'");
                if (rs.next()) {
                    return new CorredorSemArmazenamento((Integer)key);
                }

            }
            return l;
        }catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * Insere uma localização na base de dados
     * @param key id da localização
     * @param value localização
     * @return localização adicionada
     * @throws NullPointerException em caso de erro
     */
    @Override
    public Localizacao put(Integer key, Localizacao value) throws NullPointerException{

        Connection conn = ConfigDAO.connect();
        try {
            Localizacao pl = null;
            String sql;
            Statement stm = conn.createStatement();
            if (value instanceof Posicao) {
                Posicao value1 = (Posicao) value;
                if (this.containsKey(key)) {
                    stm.executeUpdate("UPDATE Localizacao SET " +
                            "Tipo = '" + 0 +
                            "', CodPrateleira = NULL " +
                            "WHERE IDvertice = '" + key + "'");
                    return value;
                } else {
                    sql =
                            "INSERT INTO Localizacao (IDvertice, Tipo, CodPrateleira) VALUES" +
                                    " ('" + key + "'," +
                                    "'" + 0 +
                                    "', NULL)";
                    int i = stm.executeUpdate(sql);
                }
            }
            if (value instanceof CorredorSemArmazenamento) {
                CorredorSemArmazenamento value1 = (CorredorSemArmazenamento) value;
                if (this.containsKey(key)) {
                    stm.executeUpdate("UPDATE Localizacao SET " +
                            "Tipo = '" + 0 +
                            "', CodPrateleira = NULL " +
                            "WHERE IDvertice = '" + key + "'");
                    return value;
                } else {
                    sql =
                            "INSERT INTO Localizacao (IDvertice, Tipo, CodPrateleira) VALUES" +
                                    " ('" + key + "'," +
                                    "'" + 0 +
                                    "', NULL)";
                    int i = stm.executeUpdate(sql);
                }
            }
            if (value instanceof CorredorComArmazenamento) {
                CorredorComArmazenamento value1 = (CorredorComArmazenamento) value;
                if (this.containsKey(key*10)) {
                    int j;
                    for(j = 0; j < value1.nrPrateleiras(); j++) {
                        stm.executeUpdate("UPDATE Localizacao SET " +
                                "Tipo = '" + 1 +
                                "', CodPrateleira = 'P" + this.n +
                                "'WHERE IDvertice = '" + (key*10+j) + "'");
                        this.n++;
                    }
                    return value;
                } else {
                    int j;
                    for(j = 0; j < value1.nrPrateleiras(); j++) {
                        sql =
                                "INSERT INTO Localizacao (IDvertice, Tipo, CodPrateleira) VALUES" +
                                        " ('" + (key*10+j) + "'," +
                                        "'" + 1 +
                                        "','P" + this.n + "')";
                        this.n++;
                        int i = stm.executeUpdate(sql);
                    }
                }
            }
            return value;
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * Remove uma localização da base de dados, dado o seu id
     * @param key is da localização
     * @return localização removida
     * @throws NullPointerException em caso de erro
     */
    @Override
    public Localizacao remove(Object key) throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        try {
            Localizacao l = this.get(key);
            Statement stm = conn.createStatement();
            String sql = "DELETE FROM Localizacao WHERE (`IDvertice` = " +
                    "'" + key + "');";
            int i = stm.executeUpdate(sql);
            return l;
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * Adiciona um conjunto de localizações à base de dados
     * @param m localizações a adicionar
     */
    @Override
    public void putAll(Map<? extends Integer, ? extends Localizacao> m) {
        throw new NullPointerException("Not implemented!");
    }

    /**
     * Apaga todas as localizações da base de dados
     */
    @Override
    public void clear() {
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate("DELETE FROM Localizacao");
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * @return Set de ids das localizações existentes na base de dados
     */
    @Override
    public Set<Integer> keySet() {
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("select distinct IDvertice from" +
                    " Localizacao");
            Set<Integer> res = new HashSet<>();
            while (rs.next())
                res.add(rs.getInt(1));
            return res;
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * @return coleção das localizações na base de dados
     */
    @Override
    public Collection<Localizacao> values() {
        Collection<Localizacao> col = new HashSet<>();
        Set<Entry<Integer, Localizacao>> entries = this.entrySetL(11);
        entries.forEach(x -> col.add(x.getValue()));
        return col;
    }

    /**
     * @return Set das localizações na base de dados e respetivos ids
     */
    @Override
    public Set<Entry<Integer, Localizacao>> entrySet() {
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("select distinct IDvertice from" +
                    " Localizacao");
            Set<Entry<Integer, Localizacao>> res = new HashSet<>();
            while (rs.next())
                res.add(new AbstractMap.SimpleEntry<>
                        (this.get(rs.getInt(1)).getNumero(), this.get(rs.getInt(1))));
            return res;
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * @param nrcorredores número de corredores
     * @return Set das localizações na base de dados e respetivos ids com base no número de corredores
     */
    public Set<Entry<Integer, Localizacao>> entrySetL(int nrcorredores) {
        int i;
        Set<Entry<Integer, Localizacao>> res = new HashSet<>();

        for(i=0; i<nrcorredores; i++){
            Localizacao l = this.get(i);
            res.add(new AbstractMap.SimpleEntry<>(l.getNumero(), l));
        }
        return res;
    }
}
