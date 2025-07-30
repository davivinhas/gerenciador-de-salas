package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectionDB {
    private static final String URL = "jdbc:mysql://switchback.proxy.rlwy.net:28040/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "NEqnqCpxSThEvagNFeQothEGLATXWvhU";

    // Se conecta com o banco de dados
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Erro ao conectar com o banco: " + e.getMessage());
            return null;
        }
    }

    // Encerra conexão com o Banco de Dados
    public static void endConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    // Teste de conexão com o Banco de Dados
    public static boolean testConnection() {
        Connection connection = getConnection();
        if (connection != null) {
            endConnection(connection);
            return true;
        }
        return false;
    }
    public static void main(String[] args) {
        Connection connection = getConnection();
        if (connection != null) {
            System.out.println("✅ Conexão bem-sucedida com o banco de dados!");
            endConnection(connection);
        } else {
            System.out.println("❌ Falha ao conectar com o banco de dados.");
        }
    }
