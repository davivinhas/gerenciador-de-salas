package controller;

import dao.ReservationDao;
import model.Reservation;
import model.ReservationStatus;
import util.Logger;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationController {
    private final ReservationDao reservationDao;

    public ReservationController() {
        this.reservationDao = new ReservationDao();
    }

    // Criar nova reserva
    public boolean createReservation(int userId, int spaceId, LocalDateTime startDateTime,
                                     LocalDateTime endDateTime, String description) {
        Reservation reservation = new Reservation(userId, spaceId, startDateTime, endDateTime, description);
        boolean success = reservationDao.insert(reservation);
        Logger.logIfSuccess(success, "Reserva criada - ID do usuário: " + userId + ", ID do espaço: " + spaceId);
        return success;
    }

    // Atualizar uma reserva existente
    public boolean updateReservation(Reservation reservation) {
        boolean success = reservationDao.update(reservation);
        Logger.logIfSuccess(success, "Reserva atualizada - ID: " + reservation.getId());
        return success;
    }

    // Cancelar uma reserva (alterar status para CANCELED)
    public boolean cancelReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELED);
        boolean success = reservationDao.update(reservation);
        Logger.logIfSuccess(success, "Reserva cancelada - ID: " + reservation.getId());
        return success;
    }

    // Confirmar uma reserva (alterar status para CONFIRMED)
    public boolean confirmReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        boolean success = reservationDao.update(reservation);
        Logger.logIfSuccess(success, "Reserva confirmada - ID: " + reservation.getId());
        return success;
    }

    // Concluir uma reserva (altera status para CONCLUDED)
    public boolean concludeReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CONCLUDED);
        boolean success = reservationDao.update(reservation);
        Logger.logIfSuccess(success, "Reserva concluída - ID: " + reservation.getId());
        return success;
    }

    // Alterar status de uma reserva
    public boolean changeReservationStatus(Reservation reservation, ReservationStatus newStatus) {
        ReservationStatus oldStatus = reservation.getStatus();
        reservation.setStatus(newStatus);
        boolean success = reservationDao.update(reservation);
        Logger.logIfSuccess(success, "Status da reserva alterado de " + oldStatus.getDescription() +
                " para " + newStatus.getDescription() + " - ID: " + reservation.getId());
        return success;
    }

    // Remover uma reserva
    public boolean deleteReservation(Reservation reservation) {
        boolean success = reservationDao.delete(reservation);
        Logger.logIfSuccess(success, "Reserva removida - ID: " + reservation.getId());
        return success;
    }

    // Buscar reserva por ID
    public Reservation getReservationById(int id) {
        return reservationDao.findById(id);
    }

    // Listar todas as reservas
    public List<Reservation> listAllReservations() {
        return reservationDao.listAll();
    }

    // Verificar se há conflito de horário para um espaço específico
    public boolean hasTimeConflict(int spaceId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Reservation> allReservations = reservationDao.listAll();

        for (Reservation reservation : allReservations) {
            // Verifica apenas reservas do mesmo espaço que não foram canceladas
            if (reservation.getSpaceId() == spaceId &&
                    reservation.getStatus() != ReservationStatus.CANCELED) {

                // Verifica se há sobreposição de horários
                if (isTimeOverlapping(startDateTime, endDateTime,
                        reservation.getStartDateTime(), reservation.getEndDateTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Verificar se há conflito de horário excluindo uma reserva específica (útil para atualizações)
    public boolean hasTimeConflict(int spaceId, LocalDateTime startDateTime, LocalDateTime endDateTime, int excludeReservationId) {
        List<Reservation> allReservations = reservationDao.listAll();

        for (Reservation reservation : allReservations) {
            // Exclui a reserva especificada da verificação
            if (reservation.getId() == excludeReservationId) {
                continue;
            }

            // Verifica apenas reservas do mesmo espaço que não foram canceladas
            if (reservation.getSpaceId() == spaceId &&
                    reservation.getStatus() != ReservationStatus.CANCELED) {

                // Verifica se há sobreposição de horários
                if (isTimeOverlapping(startDateTime, endDateTime,
                        reservation.getStartDateTime(), reservation.getEndDateTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Método auxiliar para verificar sobreposição de horários
    private boolean isTimeOverlapping(LocalDateTime start1, LocalDateTime end1,
                                      LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    // Validar se uma reserva pode ser criada
    public boolean validateReservation(int userId, int spaceId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Verifica se a data de início é anterior à data de fim
        if (!startDateTime.isBefore(endDateTime)) {
            return false;
        }

        // Verifica se a reserva não é no passado
        if (startDateTime.isBefore(LocalDateTime.now())) {
            return false;
        }

        // Verifica conflitos de horário
        return !hasTimeConflict(spaceId, startDateTime, endDateTime);
    }
}