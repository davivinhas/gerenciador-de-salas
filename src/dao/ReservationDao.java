package dao;

import model.Classroom;
import model.Reservation;
import model.ReservationStatus;
import model.Space;
import util.connectionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReservationDao implements DataAccessObject<Reservation> {

    @Override
    public boolean insert(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, space_id, status, start_datetime, end_datetime, description, creation_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement stmt = Objects.requireNonNull(connection).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, reservation.getUserId());
            stmt.setInt(2, reservation.getSpaceId());
            stmt.setString(3, reservation.getStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(reservation.getStartDateTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(reservation.getEndDateTime()));
            stmt.setString(6, reservation.getDescription());
            stmt.setTimestamp(7, Timestamp.valueOf(reservation.getCreationDate()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    reservation.setId(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir reserva: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Reservation reservation) {
        String sql = "UPDATE reservations SET user_id = ?, space_id = ?, status = ?, start_datetime = ?, end_datetime = ?, description = ? WHERE id = ?";
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement stmt = Objects.requireNonNull(connection).prepareStatement(sql);
            stmt.setInt(1, reservation.getUserId());
            stmt.setInt(2, reservation.getSpaceId());
            stmt.setString(3, reservation.getStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(reservation.getStartDateTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(reservation.getEndDateTime()));
            stmt.setString(6, reservation.getDescription());
            stmt.setInt(7, reservation.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar reserva: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(Reservation reservation) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement stmt = Objects.requireNonNull(connection).prepareStatement(sql);
            stmt.setInt(1, reservation.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar reserva: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Reservation findById(int id) {
        String sql = "SELECT r.*, s.name AS space_name FROM reservations r LEFT JOIN spaces s ON r.space_id = s.id WHERE r.id = ?";
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement stmt = Objects.requireNonNull(connection).prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToReservation(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar reserva: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Reservation> listAll() {
        String sql = "SELECT r.*, s.name AS space_name FROM reservations r LEFT JOIN spaces s ON r.space_id = s.id ORDER BY r.start_datetime";
        List<Reservation> reservations = new ArrayList<>();
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement stmt = Objects.requireNonNull(connection).prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar reservas: " + e.getMessage());
        }
        return reservations;
    }

    public List<Reservation> listByUserId(int userId) {
        String sql = "SELECT r.*, s.name AS space_name FROM reservations r LEFT JOIN spaces s ON r.space_id = s.id WHERE r.user_id = ? ORDER BY r.start_datetime";
        List<Reservation> reservations = new ArrayList<>();
        try {
            Connection connection = connectionDB.getConnection();
            PreparedStatement stmt = Objects.requireNonNull(connection).prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar reservas por usuário: " + e.getMessage());
        }
        return reservations;
    }

    // Método para mapear ResultSet para Reservation
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setSpaceId(rs.getInt("space_id"));
        r.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        r.setStartDateTime(rs.getTimestamp("start_datetime").toLocalDateTime());
        r.setEndDateTime(rs.getTimestamp("end_datetime").toLocalDateTime());
        r.setDescription(rs.getString("description"));
        r.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());

        // Mapeia o espaço se existir
        String spaceName = rs.getString("space_name");
        if (spaceName != null) {
            Space space = new Classroom(); // ou qualquer subclasse concreta
            space.setId(rs.getInt("space_id"));
            space.setName(spaceName);
            r.setSpace(space);
        }

        return r;
    }
}