package dao;

import model.Reservation;
import model.ReservationStatus;
import util.connectionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class
ReservationDao implements DataAccessObject<Reservation> {

    @Override
    public boolean insert(Reservation reservation) {
        Connection connection = null;
        try {
            connection = connectionDB.getConnection();
            String sql = "INSERT INTO reservations (user_id, space_id, status, start_datetime, end_datetime, description, creation_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
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
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean update(Reservation reservation) {
        Connection connection = null;
        try {
            connection = connectionDB.getConnection();
            String sql = "UPDATE reservations SET user_id = ?, space_id = ?, status = ?, start_datetime = ?, end_datetime = ?, description = ? WHERE id = ?";
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
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean delete(Reservation reservation) {
        Connection connection = null;
        try {
            connection = connectionDB.getConnection();
            String sql = "DELETE FROM reservations WHERE id = ?";
            PreparedStatement stmt = Objects.requireNonNull(connection).prepareStatement(sql);
            stmt.setInt(1, reservation.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar reserva: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public Reservation findById(int id) {
        Connection connection = null;
        try {
            connection = connectionDB.getConnection();
            String sql = "SELECT * FROM reservations WHERE id = ?";
            PreparedStatement stmt = Objects.requireNonNull(connection).prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToReservation(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar reserva: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public List<Reservation> listAll() {
        List<Reservation> reservations = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionDB.getConnection();
            String sql = "SELECT * FROM reservations ORDER BY start_datetime";
            PreparedStatement stmt = Objects.requireNonNull(connection).prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar reservas: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        return reservations;
    }

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
        return r;
    }

    // Você pode adicionar métodos auxiliares, como buscar por status, usuário, ou espaço se quiser.
}
