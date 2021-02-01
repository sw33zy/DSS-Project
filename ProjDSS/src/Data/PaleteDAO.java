package Data;


import Business.Palete;
import Business.Localizacao;
import Business.Posicao;
import Business.Prateleira;

import java.sql.*;
import java.util.*;


public class PaleteDAO implements Map<String, Palete> {
    private static PaleteDAO singleton = null;

    private PaleteDAO() {
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
                    "CodPrateleira varchar(10) DEFAULT NULL," +
                    "FOREIGN KEY(CodPrateleira) REFERENCES Prateleiras(CodPrateleira))" ;
            stm.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS Paletes (" +
                    "codPalete varchar(10) NOT NULL PRIMARY KEY," +
                    "Estado BOOLEAN NOT NULL," +
                    "EstadoTransp BOOLEAN NOT NULL," +
                    "Altura int(4) NOT NULL DEFAULT 0,"+
                    "Localizacao int(4) NOT NULL DEFAULT 0, foreign key(Localizacao) references Localizacao(IDvertice))";
            stm.executeUpdate(sql);
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * Implementação do padrão Singleton
     * @return instância única desta classe
     */
    public static PaleteDAO getInstance() {
        if (PaleteDAO.singleton == null) {
            PaleteDAO.singleton = new PaleteDAO();
        }
        return PaleteDAO.singleton;
    }

    /**
     * @return número de paletes na base de dados
     * @throws NullPointerException em caso de erro
     */
    @Override
    public int size() throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        int i = 0;
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT count(*) FROM Paletes") ;
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
     * Verifica se existem paletes
     * @return true se existirem 0 paletes
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Certifica se o código de uma palete existe na base de dados
     * @param key código da palete
     * @return true se a palete já existe
     * @throws NullPointerException em caso de erro
     */
    @Override
    public boolean containsKey(Object key) throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        boolean r;
        try {
            Statement stm = conn.createStatement();
            ResultSet rs =
                    stm.executeQuery("SELECT codPalete FROM Paletes WHERE codPalete='"+key.toString()+"'");
            r = rs.next();
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return r;
    }

    /**
     * Verifica se uma palete já existe na base de dados
     * @param value palete
     * @return true se palete já existe
     */
    @Override
    public boolean containsValue(Object value) {
        Palete p = (Palete) value;
        boolean r = false;
        Palete c = this.get(p.getCodigo());
        if (c!=null){
            if(p.getDisponivel() == c.getDisponivel())
                if(p.getAltura() == c.getAltura())
                    r = p.getLoc() == c.getLoc();
        }
        return r;
    }

    /**
     * Obtém uma palete dado o seu código
     * @param key código da palete
     * @return palete, caso exista, senão null
     * @throws NullPointerException em caso de erro
     */
    @Override
    public Palete get(Object key) throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        Palete p = null;
        Localizacao loc = new Posicao();
        Prateleira ptl = null;
        try  {
            Statement stm = conn.createStatement();
            //???????????????
            ResultSet rs = stm.executeQuery("SELECT * FROM Paletes p " +
                    "INNER JOIN Localizacao l  on p.Localizacao = l.IDvertice " +
                    "WHERE p.codPalete = '"+key+"'");
            if (rs.next()) {  // A chave existe na tabela
                loc.setNumero(rs.getInt("IDvertice"));
                p = new Palete(rs.getString("codPalete"), rs.getInt("Altura"), loc, rs.getBoolean("Estado"),rs.getBoolean("EstadoTransp"));
            }
        } catch (SQLException e) {
            // Database error!
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
        return p;
    }

    /**
     * Insere uma palete na base de dados
     * @param key código da palete
     * @param value palete
     * @return palete adicionada
     * @throws NullPointerException em caso de erro
     */
    @Override
    public Palete put(String key, Palete value) throws NullPointerException{
        Palete pl = null;
        String sql;
        int estado;
        if(value.getDisponivel()) estado = 1;
        else estado=0;
        int estadoT;
        if(value.getEmTransporte()) estadoT = 1;
        else estadoT=0;
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            if (this.containsKey(key)) {
                stm.executeUpdate("UPDATE Paletes SET " +
                        "Estado = '" + estado +
                        "', EstadoTransp = '" + estadoT +
                        "', Altura = '" + value.getAltura() +
                        "', Localizacao = '" + value.getLoc().getNumero() +
                        "'WHERE codPalete = '" + key + "'");
                return value;
            }
            else {
                sql =
                        "INSERT INTO Paletes (codPalete, Estado, EstadoTransp, Altura, Localizacao) VALUES" +
                                " ('" + key + "'," +
                                "'" + estado +
                                "','" + estadoT +
                                "','" + value.getAltura() +
                                "','" + value.getLoc().getNumero() + "')";
            }
            int i = stm.executeUpdate(sql);
            return new Palete(value.getCodigo(), value.getAltura(), value.getLoc(), value.getDisponivel(), value.getEmTransporte());
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * Remove uma palete, dado o seu código
     * @param key código da palete
     * @return palete removida
     * @throws NullPointerException em caso de erro
     */
    @Override
    public Palete remove(Object key) throws NullPointerException{
        Connection conn = ConfigDAO.connect();
        try {
            Palete p = this.get(key);
            Statement stm = conn.createStatement();
            String sql = "DELETE FROM Paletes WHERE (`codPalete` = " +
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
     * Adiciona um conjunto de paletes à base de dados
     * @param m paletes a adicionar
     */
    @Override
    public void putAll(Map<? extends String, ? extends Palete> m) {
        throw new NullPointerException("Not implemented!");
    }

    /**
     * Apaga todas as paletes da base de dados
     */
    @Override
    public void clear() {
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate("DELETE FROM Paletes");
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }

    /**
     * @return Set de códigos das paletes existentes na base de dados
     */
    @Override
    public Set<String> keySet() {
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("select distinct codPalete from" +
                    " Paletes");
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
     * @return coleção de paletes existentes na base de dados
     */
    @Override
    public Collection<Palete> values() {
        Collection<Palete> col = new HashSet<>();
        Set<String> keys = this.keySet();
        keys.forEach(x -> col.add(this.get(x)));
        return col;
    }

    @Override
    public Set<Entry<String, Palete>> entrySet() {
        throw new NullPointerException("Not implemented!");
    }

    /**
     * @return códigos das paletes a aguardar transporte
     */
    public Queue<String> queueTransporte(){
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("select distinct codPalete from" +
                    " Paletes p " + " WHERE p.Estado = 0");
            Queue<String> res = new ArrayDeque<String>();
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
     * @return Map das posições das paletes existentes na base de dados, identificadas pelo seu código
     */
    public Map<String, Posicao> listagem(){
        Connection conn = ConfigDAO.connect();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Paletes p");
            Map<String, Posicao> res= new HashMap<>();
            while (rs.next()) {
                if (rs.getString("Localizacao").charAt(0) != '0') {
                    res.put(rs.getString("codPalete"),
                            new Posicao(Character.getNumericValue(rs.getString("Localizacao").charAt(0)),
                                    Character.getNumericValue(rs.getString("Localizacao").charAt(1))));
                }
                else
                    if(!rs.getBoolean("EstadoTransp"))
                        res.put(rs.getString("codPalete"), new Posicao(0, 0));
                    else
                        res.put(rs.getString("codPalete"), new Posicao(99, 0));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        } finally {
            ConfigDAO.close(conn);
        }
    }
}
