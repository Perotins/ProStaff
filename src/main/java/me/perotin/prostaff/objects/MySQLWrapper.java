package me.perotin.prostaff.objects;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.perotin.prostaff.ProStaff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLWrapper {

    private String user;
    private String database;
    private String password;
    private String host;
    private HikariDataSource hikariDataSource;


    public MySQLWrapper(ProStaff plugin) {
        this.user = plugin.getConfig().getString("user");
        this.database = plugin.getConfig().getString("database");
        this.password = plugin.getConfig().getString("password");
        this.host = plugin.getConfig().getString("host");

    }


    public void init() {
        // configuring HikariConfig object
        HikariConfig config;
        config = new HikariConfig();
        config.setJdbcUrl("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(15);
        config.addDataSourceProperty("databaseName", database);
        config.addDataSourceProperty("serverName", host);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        this.hikariDataSource = new HikariDataSource(config);

    }


    private void cleanup() {
        hikariDataSource.close();

    }

    public void addAllRanks() {

        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE StaffRanks");
            statement.executeUpdate();
            statement = connection.prepareStatement("TRUNCATE TABLE StaffRankMembers");
            statement.executeUpdate();



            if (!ProStaff.getInstance().getRanks().isEmpty()) {
                for (StaffRank rank : ProStaff.getInstance().getRanks()) {
                    String sql = "INSERT INTO StaffRanks VALUES(?, ?, ?);";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, rank.getName());
                    statement.setInt(2, rank.getPower());
                    statement.setInt(3, rank.getColor());
                    statement.executeUpdate();

                    for (UUID uuid : rank.getUuids()) {
                        sql = "INSERT INTO StaffRankMembers VALUES (?, ?);";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, rank.getName());
                        statement.setString(2, uuid.toString());
                        statement.executeUpdate();
                    }

                }
            }

            statement.close();
            cleanup();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public List<StaffRank> loadRanks() {
        List<StaffRank> ranks = new ArrayList<>();
        try (Connection connection = hikariDataSource.getConnection()) {
            String query1 = "SELECT * FROM StaffRanks";
            PreparedStatement statement = connection.prepareStatement(query1);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                String name = results.getString("name");
                int power = results.getInt("power");
                int color = results.getInt("color");
                String query2 = "SELECT * FROM StaffRankMembers WHERE rank = ?";
                statement = connection.prepareStatement(query2);
                statement.setString(1, name);
                ResultSet res2 = statement.executeQuery();

                List<UUID> members = new ArrayList<>();
                while (res2.next()) {
                    String uuid = res2.getString("uuid");
                    members.add(UUID.fromString(uuid));

                }

                ranks.add(new StaffRank(name, power, members, color));
                res2.close();
            }
            results.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ranks;
    }

    public void createTables() {

        try (Connection connection = hikariDataSource.getConnection()) {

            PreparedStatement create = connection.prepareStatement("CREATE TABLE IF NOT EXISTS StaffRanks (name VARCHAR(50) NOT NULL PRIMARY KEY, power INT NOT NULL, color INT NOT NULL)");
            create.executeUpdate();

            create = connection.prepareStatement("CREATE TABLE IF NOT EXISTS StaffRankMembers" +
                    "(rank VARCHAR(50) NOT NULL, uuid VARCHAR(36) NOT NULL, PRIMARY KEY (rank, uuid))");
            create.executeUpdate();
            create.close();
        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }


//    public String getDatabase() {
//        return this.database;
//    }


}
