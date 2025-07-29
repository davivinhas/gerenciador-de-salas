package dao;

import model.User;
import model.UserType;
import util.connectionDB;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UserDao implements DataAccessObject<User>{

    // Métodos de interação com banco de dados
    @Override
    public boolean insert(User user){
        String sql = "INSERT INTO users (name, email, password, type, creation_date) VALUES (?, ?, ?, ?, ?)";

        try{
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(sql);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getType().getDescription()); //testa dps .name()
            statement.setTimestamp(5, Timestamp.valueOf(user.getCreationDate()));
            return statement.executeUpdate() > 0;
        }catch (SQLException e){
            System.err.println("Erro ao inserir usuário: " + e.getMessage());
        }
        return false;
    }
    @Override
    public boolean update(User user){
            String sql = "UPDATE users SET name = ?, email = ?, password = ?, type = ?, creation_date = ? WHERE id = ?";
            try{
                Connection connection = connectionDB.getConnection();
                PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(sql);
                statement.setString(1, user.getName());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPassword());
                statement.setString(4, user.getType().getDescription());
                statement.setTimestamp(5, Timestamp.valueOf(user.getCreationDate()));
                statement.setInt(6, user.getId());
                return statement.executeUpdate() > 0;
            }catch(SQLException e){
                System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            }
            return false;

    }
    @Override
    public boolean delete(User user){
        String sql = "DELETE FROM user WHERE id = ?";
        try{
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(sql);
            statement.setInt(1, user.getId());
            return statement.executeUpdate() > 0;

        }catch(SQLException e){
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
        }
        return false;
    }
    @Override
    public User findById(int id){
        String sql = "SELECT * FROM user WHERE id = ?";
        try{
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return mapResultSetToUser(resultSet);

            }
        }catch(SQLException e){
            System.err.println("Usuário não encontrado: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> listAll(){
        String sql = "SELECT * FROM user";
        List<User> users = new ArrayList<>();
        try{
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                users.add(mapResultSetToUser(resultSet));
            }
            return users;
        }catch(SQLException e){
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }
        return null;
    }

    public User findByEmail(String email){
        String sql = "SELECT * FROM user WHERE email = ?";
        try{
            Connection connection = connectionDB.getConnection();
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return mapResultSetToUser(resultSet);
            }
        }catch(SQLException e){
            System.out.println("Não foi possível encontrar usuário com esse email: " + e.getMessage());
        }
        return null;
    }

    public boolean validateLogin(String email, String password){
        User user = findByEmail(email);
        return user != null && user.getPassword().equals(password);
    }

    // Método para receber usuário do banco de dados
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException{
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setType(UserType.valueOf(resultSet.getString("type")));
        user.setCreationDate(resultSet.getTimestamp("creation_date").toLocalDateTime());
        return user;
    }
}
