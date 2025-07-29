package controller;

import dao.UserDao;
import model.User;
import model.UserType;
import util.Logger;
import java.util.List;
import java.util.stream.Collectors;

public class UserController {
    private final UserDao userDao;

    public UserController() {
        this.userDao = new UserDao();
    }

    // Criar usuário
    public boolean createUser(String name, String email, String password, UserType userType) { //cria o user
        User user = new User(0, name, email, password, userType);
        boolean success = userDao.insert(user);
        Logger.logIfSuccess(success, "Usuário criado: " + email);
        return success;
    }

    // Atualizar usuário
    public boolean updateUser(User user) {  //atualiza o user
        boolean success = userDao.update(user);
        Logger.logIfSuccess(success, "Usuário atualizado: " + user.getEmail());
        return success;
    }

   // Deletar usuário
    public boolean deleteUser(User user) { //remove user
        boolean success = userDao.delete(user);
        Logger.logIfSuccess(success, "Usuário removido: " + user.getEmail());
        return success;
    }

    // Fazer login e retornar usuário
    public User login(String email, String password) {
        if (userDao.validateLogin(email, password)) {
            User user = userDao.findByEmail(email);
            Logger.logIfSuccess(user != null, "Login bem-sucedido: " + email);
            return user;
        }
        return null;
    }

    public User getUserById(int id) { //busca usre id
        return userDao.findById(id);
    }
    public User getUserByEmail(String email) { //busca user email
        return userDao.findByEmail(email);
    }
    public List<User> listAllUsers() { //lista users
        return userDao.listAll();
    }
    public List<User> listUsersByType(UserType userType) { //filtra users tipo
        List<User> allUsers = userDao.listAll();
        if (allUsers == null) return List.of();
        return allUsers.stream()
                .filter(user -> user.getType() == userType)
                .collect(Collectors.toList());
    }

    // Fazer login e retornar usuário
    public boolean authenticateUser(String email, String password) { //autenticacao
        boolean success = userDao.validateLogin(email, password);
        Logger.logIfSuccess(success, "Login realizado: " + email);
        return success;
    }

    // Alterar senha
    public boolean changePassword(User user, String newPassword) { //muda senha
        user.setPassword(newPassword);
        boolean success = userDao.update(user);
        Logger.logIfSuccess(success, "Senha alterada para: " + user.getEmail());
        return success;
    }

    // Verificar se email já existe
    public boolean emailExists(String email) {
        return userDao.findByEmail(email) != null;
    }

    // Validar formato do email
    private boolean isEmailValid(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    // Verificar se usuário é admin
    public boolean isAdmin(User user) {
        return user != null && user.getType() == UserType.ADMIN;
    }

    // Criar admin com senha especial
    public boolean createAdmin(String name, String email, String password, String adminKey) {
        if (!"ADMIN_KEY_2024".equals(adminKey)) {
            return false;
        }
        return createUser(name, email, password, UserType.ADMIN);
    }

}
