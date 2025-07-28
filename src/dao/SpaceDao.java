package dao;

import model.*;
import java.sql.*;
import util.connectionDB;
import java.util.*;


public class SpaceDao implements DataAccessObject<Space>{
    //Métodos para qualquer tipo
    @Override
    public boolean insert(Space space) {
        Connection connection = null;
        try {
            connection = connectionDB.getConnection();
            connection.setAutoCommit(false); // Inicia transação

            // Insere na tabela spaces
            String spaceSql = "INSERT INTO spaces (name, localization, capacity, available_hours, creation_date, space_type) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement spaceStatement = Objects.requireNonNull(connection).prepareStatement(spaceSql, Statement.RETURN_GENERATED_KEYS);

            spaceStatement.setString(1, space.getName());
            spaceStatement.setString(2, space.getLocalization());
            spaceStatement.setInt(3, space.getCapacity());
            spaceStatement.setString(4, space.getAvailableHours());
            spaceStatement.setTimestamp(5, Timestamp.valueOf(space.getCreationDate()));
            spaceStatement.setString(6, space.getType().toUpperCase());

            int rowsAffected = spaceStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Recupera o ID gerado
                ResultSet generatedKeys = spaceStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int spaceId = generatedKeys.getInt(1);
                    space.setId(spaceId);

                    // Insere na tabela específica baseada no tipo
                    boolean specificInsert = insertSpecificSpace(connection, space);

                    if (specificInsert) {
                        connection.commit();
                        return true;
                    }
                }
            }

            connection.rollback();
            return false;

        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erro ao fazer rollback: " + ex.getMessage());
            }
            System.err.println("Erro ao inserir espaço: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean update(Space space) {
        Connection connection = null;
        try {
            connection = connectionDB.getConnection();
            connection.setAutoCommit(false);

            // Atualiza tabela spaces
            String spaceSql = "UPDATE spaces SET name = ?, localization = ?, capacity = ?, available_hours = ? WHERE id = ?";
            PreparedStatement spaceStatement = Objects.requireNonNull(connection).prepareStatement(spaceSql);

            spaceStatement.setString(1, space.getName());
            spaceStatement.setString(2, space.getLocalization());
            spaceStatement.setInt(3, space.getCapacity());
            spaceStatement.setString(4, space.getAvailableHours());
            spaceStatement.setInt(5, space.getId());

            int rowsAffected = spaceStatement.executeUpdate();
            if (rowsAffected > 0) {
                boolean specificUpdate = updateSpecificSpace(connection, space);
                if (specificUpdate) {
                    connection.commit();
                    return true;
                }
            }

            connection.rollback();
            return false;

        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erro ao fazer rollback: " + ex.getMessage());
            }
            System.err.println("Erro ao atualizar espaço: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean delete(Space space) {
        String sql = "DELETE FROM spaces WHERE id = ?";
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(sql);
            statement.setInt(1, space.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar espaço: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Space findById(int id) {
        String sql = "SELECT * FROM spaces WHERE id = ?";
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToSpace(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar espaço: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Space> listAll() {
        String sql = "SELECT * FROM spaces ORDER BY name";
        List<Space> spaces = new ArrayList<>();
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Space space = mapResultSetToSpace(resultSet);
                if (space != null) {
                    spaces.add(space);
                }
            }
            return spaces;
        } catch (SQLException e) {
            System.err.println("Erro ao listar espaços: " + e.getMessage());
        }
        return spaces;
    }

    //Método específico para encontrar por tipo de espaço
    public List<Space> findByType(String type){
        String sql = "SELECT * FROM spaces WHERE space_type = ?";
        List <Space> spaces = new ArrayList<>();
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, type.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Space space = mapResultSetToSpace(resultSet);
                if (space != null) {
                    spaces.add(space);
                }
            }
            return spaces;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar espaços por tipo: " + e.getMessage());
        }
        return spaces;
    }

    // Método privado para mapear ResultSet para Space
    private Space mapResultSetToSpace(ResultSet resultSet) throws SQLException {
        String spaceType = resultSet.getString("space_type");
        Space space = null;
        switch (spaceType) {
            case "CLASSROOM":
                space = mapToClassroom(resultSet);
                break;
            case "LABORATORY":
                space = mapToLaboratory(resultSet);
                break;
            case "MEETING_ROOM":
                space = mapToMeetingRoom(resultSet);
                break;
            case "AUDITORIUM":
                space = mapToAuditorium(resultSet);
                break;
            case "SPORTS_FIELD":
                space = mapToSportsField(resultSet);
                break;
        }
        if (space != null) {
            space.setId(resultSet.getInt("id"));
            space.setName(resultSet.getString("name"));
            space.setLocalization(resultSet.getString("localization"));
            space.setCapacity(resultSet.getInt("capacity"));
            space.setAvailableHours(resultSet.getString("available_hours"));
            space.setCreationDate(resultSet.getTimestamp("creation_date").toLocalDateTime());
        }
        return space;
    }

    // Métodos privados para espaço específico
    private boolean insertSpecificSpace(Connection connection, Space space) throws SQLException {
        switch (space.getType()) {
            case "Classroom":
                return insertClassroom(connection, (Classroom) space);
            case "Laboratory":
                return insertLaboratory(connection, (Laboratory) space);
            case "MeetingRoom":
                return insertMeetingRoom(connection, (MeetingRooms) space);
            case "Auditorium":
                return insertAuditorium(connection, (Auditorium) space);
            case "SportsField":
                return insertSportsField(connection, (SportsField) space);
            default:
                return false;
        }
    }

    private boolean updateSpecificSpace(Connection connection, Space space) throws SQLException {
        switch (space.getType()) {
            case "Classroom":
                return updateClassroom(connection, (Classroom) space);
            case "Laboratory":
                return updateLaboratory(connection, (Laboratory) space);
            case "MeetingRoom":
                return updateMeetingRoom(connection, (MeetingRooms) space);
            case "Auditorium":
                return updateAuditorium(connection, (Auditorium) space);
            case "SportsField":
                return updateSportsField(connection, (SportsField) space);
            default:
                return false;
        }
    }

    // Métodos para Classroom
    private boolean insertClassroom(Connection connection, Classroom classroom) throws SQLException {
        String sql = "INSERT INTO classrooms (space_id, num_of_boards, num_of_data_shows) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, classroom.getId());
        statement.setInt(2, classroom.getNumOfBoards());
        statement.setInt(3, classroom.getNumOfDataShows());
        return statement.executeUpdate() > 0;
    }

    private boolean updateClassroom(Connection connection, Classroom classroom) throws SQLException {
        String sql = "UPDATE classrooms SET num_of_boards = ?, num_of_data_shows = ? WHERE space_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, classroom.getNumOfBoards());
        statement.setInt(2, classroom.getNumOfDataShows());
        statement.setInt(3, classroom.getId());
        return statement.executeUpdate() > 0;
    }

    private Classroom mapToClassroom(ResultSet resultSet) throws SQLException {
        int spaceId = resultSet.getInt("id");
        String sql = "SELECT * FROM classrooms WHERE space_id = ?";

        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, spaceId);
            ResultSet classroomRS = statement.executeQuery();

            if (classroomRS.next()) {
                Classroom classroom = new Classroom();
                classroom.setNumOfBoards(classroomRS.getInt("num_of_boards"));
                classroom.setNumOfDataShows(classroomRS.getInt("num_of_data_shows"));
                return classroom;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao mapear classroom: " + e.getMessage());
        }
        return null;
    }

    // Métodos para Laboratory
    private boolean insertLaboratory(Connection connection, Laboratory laboratory) throws SQLException {
        String sql = "INSERT INTO laboratories (space_id, num_of_computers, lab_type) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, laboratory.getId());
        statement.setInt(2, laboratory.getNumOfComputers());
        statement.setString(3, laboratory.getLabType());
        return statement.executeUpdate() > 0;
    }

    private boolean updateLaboratory(Connection connection, Laboratory laboratory) throws SQLException {
        String sql = "UPDATE laboratories SET num_of_computers = ?, lab_type = ? WHERE space_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, laboratory.getNumOfComputers());
        statement.setString(2, laboratory.getLabType());
        statement.setInt(3, laboratory.getId());
        return statement.executeUpdate() > 0;
    }

    private Laboratory mapToLaboratory(ResultSet resultSet) throws SQLException {
        int spaceId = resultSet.getInt("id");
        String sql = "SELECT * FROM laboratories WHERE space_id = ?";

        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, spaceId);
            ResultSet labRS = statement.executeQuery();

            if (labRS.next()) {
                Laboratory laboratory = new Laboratory();
                laboratory.setNumOfComputers(labRS.getInt("num_of_computers"));
                laboratory.setLabType(labRS.getString("lab_type"));
                return laboratory;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao mapear laboratory: " + e.getMessage());
        }
        return null;
    }

    // Métodos para MeetingRoom
    private boolean insertMeetingRoom(Connection connection, MeetingRooms meetingRoom) throws SQLException {
        String sql = "INSERT INTO meeting_rooms (space_id, num_of_projectors, num_of_video_conferences) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, meetingRoom.getId());
        statement.setInt(2, meetingRoom.getNumOfProjectors());
        statement.setInt(3, meetingRoom.getNumOfVideoConferences());
        return statement.executeUpdate() > 0;
    }

    private boolean updateMeetingRoom(Connection connection, MeetingRooms meetingRoom) throws SQLException {
        String sql = "UPDATE meeting_rooms SET num_of_projectors= ?, num_of_video_conferences = ? WHERE space_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, meetingRoom.getNumOfProjectors());
        statement.setInt(2, meetingRoom.getNumOfVideoConferences());
        statement.setInt(3, meetingRoom.getId());
        return statement.executeUpdate() > 0;
    }

    private MeetingRooms mapToMeetingRoom(ResultSet resultSet) throws SQLException {
        int spaceId = resultSet.getInt("id");
        String sql = "SELECT * FROM meeting_rooms WHERE space_id = ?";

        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, spaceId);
            ResultSet meetingRS = statement.executeQuery();

            if (meetingRS.next()) {
                MeetingRooms meetingRoom = new MeetingRooms();
                meetingRoom.setNumOfProjectors(meetingRS.getInt("num_of_projectors"));
                meetingRoom.setNumOfVideoConferences(meetingRS.getInt(("num_of_video_conferences")));
                return meetingRoom;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao mapear meeting room: " + e.getMessage());
        }
        return null;
    }

    // Métodos para Auditorium
    private boolean insertAuditorium(Connection connection, Auditorium auditorium) throws SQLException {
        String sql = "INSERT INTO auditoriums (space_id, num_of_sound_systems, num_of_stages) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, auditorium.getId());
        statement.setInt(2, auditorium.getNumOfSoundSystems());
        statement.setInt(3, auditorium.getNumOfStages());
        return statement.executeUpdate() > 0;
    }

    private boolean updateAuditorium(Connection connection, Auditorium auditorium) throws SQLException {
        String sql = "UPDATE auditoriums SET num_of_sound_systems = ?, num_of_stages = ? WHERE space_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, auditorium.getNumOfSoundSystems());
        statement.setInt(2, auditorium.getNumOfStages());
        statement.setInt(3, auditorium.getId());
        return statement.executeUpdate() > 0;
    }

    private Auditorium mapToAuditorium(ResultSet resultSet) throws SQLException {
        int spaceId = resultSet.getInt("id");
        String sql = "SELECT * FROM auditoriums WHERE space_id = ?";

        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, spaceId);
            ResultSet auditoriumRS = statement.executeQuery();

            if (auditoriumRS.next()) {
                Auditorium auditorium = new Auditorium();
                auditorium.setNumOfSoundSystems(auditoriumRS.getInt("num_of_sound_systems"));
                auditorium.setNumOfStages(auditoriumRS.getInt("num_of_stages"));
                return auditorium;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao mapear auditorium: " + e.getMessage());
        }
        return null;
    }

    // Métodos para SportsField
    private boolean insertSportsField(Connection connection, SportsField sportsField) throws SQLException {
        String sql = "INSERT INTO sports_fields (space_id, num_of_goals, num_of_hoops, num_of_nets) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, sportsField.getId());
        statement.setInt(2, sportsField.getNumOfGoals());
        statement.setInt(3, sportsField.getNumOfHoops());
        return statement.executeUpdate() > 0;
    }

    private boolean updateSportsField(Connection connection, SportsField sportsField) throws SQLException {
        String sql = "UPDATE sports_fields SET num_of_nets = ?, num_of_hoops = ?, num_of_goals = ? WHERE space_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, sportsField.getNumOfGoals());
        statement.setInt(2, sportsField.getNumOfHoops());
        statement.setInt(3, sportsField.getNumOfGoals());
        statement.setInt(4, sportsField.getId());
        return statement.executeUpdate() > 0;
    }

    private SportsField mapToSportsField(ResultSet resultSet) throws SQLException {
        int spaceId = resultSet.getInt("id");
        String sql = "SELECT * FROM sports_fields WHERE space_id = ?";

        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, spaceId);
            ResultSet sportsRS = statement.executeQuery();

            if (sportsRS.next()) {
                SportsField sportsField = new SportsField();
                sportsField.setNumOfHoops(sportsRS.getInt("num_of_hoops"));
                sportsField.setNumOfGoals(sportsRS.getInt("num_of_goals"));
                sportsField.setNumOfNets(sportsRS.getInt("num_of_nets"));
                return sportsField;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao mapear sports field: " + e.getMessage());
        }
        return null;
    }
}
