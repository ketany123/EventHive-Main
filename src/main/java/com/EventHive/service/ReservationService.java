package com.EventHive.service;


import com.EventHive.dto.ReservationRequestDto;
import com.EventHive.dto.UserResponseDto;
import com.EventHive.entity.Reservation;
import com.EventHive.entity.Seat;
import com.EventHive.entity.User;
import com.EventHive.entity.Wallet;
import com.EventHive.enums.ReservationStatus;
import com.EventHive.enums.SeatStatus;
import com.EventHive.exception.*;
import com.EventHive.repository.ReservationRepository;
import com.EventHive.repository.SeatRepository;
import com.EventHive.repository.ShowRepository;
import com.EventHive.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static com.EventHive.constant.ExceptionMessages.*;

@Service
public class ReservationService {


    @Autowired
    private QrCodeService qrCodeService;

    private final SeatLockManager seatLockManager;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReservationService(SeatLockManager seatLockManager,
                              ReservationRepository reservationRepository,
                              SeatRepository seatRepository,
                              ShowRepository showRepository,
                              UserRepository userRepository) {
        this.seatLockManager = seatLockManager;
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Reservation createReservation(ReservationRequestDto reservationRequestDto, String currentUserName) {

        return showRepository
                .findById(reservationRequestDto.getShowId())
                .map(show -> {
                    List<Seat> seats = reservationRequestDto
                            .getSeatIdsToReserve()
                            .stream()
                            .map(seatRepository::findById)
                            .map(Optional::get)
                            .toList();

                    // Calculate the amount to be paid.
                    Double amountToBePaid = seats.stream().map(Seat::getPrice).reduce(0.0, Double::sum);
                    Optional<User> byUsername = userRepository.findByUsername(currentUserName);
                    User user1=byUsername.get();
                    Wallet wallet=user1.getWallet();
                    int amount= wallet.getAmount();
                    int amountPaid= (amountToBePaid.intValue());

                    if(amount < amountPaid)
                        throw new AmountNotMatchException(AMOUNT_NOT_MATCH, HttpStatus.BAD_REQUEST);


                    amount -=  amountPaid;
                    wallet.setAmount(amount);
                    userRepository.save(user1);
                    // Acquire the lock for all seats
                    seats.forEach(seat -> {
                        ReentrantLock seatLock = seatLockManager.getLockForSeat(seat.getId());
                        boolean isLockFree = seatLock.tryLock();
                        if (!isLockFree){
                            throw new SeatLockAcquiredException(SEAT_LOCK_ACQUIRED, HttpStatus.CONFLICT);
                        }
                    });

                    boolean anyBookedSeat = seats.stream().map(Seat::getStatus).anyMatch(seatStatus -> seatStatus.equals(SeatStatus.BOOKED));

                    if (anyBookedSeat){
                        // Remove lock for every seat
                        seats.forEach(seat -> seatLockManager.removeLockForSeat(seat.getId()));
                        throw new SeatAlreadyBookedException(SEAT_ALREADY_BOOKED, HttpStatus.BAD_REQUEST);
                    }

                    // Mark all the seats as booked
                    List<Seat> bookedSeats = seats.stream().map(seat -> {
                        seat.setStatus(SeatStatus.BOOKED);
                        return seatRepository.save(seat);
                    }).toList();

                    User user = userRepository.findByUsername(currentUserName).orElseThrow(() -> new RuntimeException("User not found"));




                    // Create the reservation
                    Reservation reservation = Reservation.builder()
                            .reservationStatus(ReservationStatus.BOOKED)
                            .seatsReserved(bookedSeats)
                            .show(show)
                            .user(user)
                            .createdAt(LocalDateTime.now())
                            .build();

                    reservation.setId(reservationRequestDto.getId());

                    long idd= reservation.getId();
                    String qrData=idd+"";
                    try{

                        byte[] qrimage=qrCodeService.generateQRCodeImage(qrData);
                        reservation.setQrImage(qrimage);
                        reservationRepository.save(reservation);
                    }catch (Exception e){
                        System.out.println("not valid not found");
                    }


                    // Remove lock for every seat
                    seats.forEach(seat -> seatLockManager.removeLockForSeat(seat.getId()));



                    return reservation;
                })
                .orElseThrow(() -> new ShowNotFoundException(SHOW_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    public Reservation getReservationById(long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(RESERVATION_NOT_FOUND, HttpStatus.NOT_FOUND));

    }

    public Reservation cancelReservation(long reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservationIdb -> {
                    if (LocalDateTime.now().isAfter(reservationIdb.getShow().getStartTime()))
                        throw new ShowStartedException(SHOW_STARTED_EXCEPTION, HttpStatus.BAD_REQUEST);

                    reservationIdb.getSeatsReserved()
                            .forEach(seat -> {
                                seat.setStatus(SeatStatus.UNBOOKED);
                                seatRepository.save(seat);
                            });

                    reservationIdb.setReservationStatus(ReservationStatus.CANCELED);
                    return reservationRepository.save(reservationIdb);
                })
                .orElseThrow(() -> new ReservationNotFoundException(RESERVATION_NOT_FOUND, HttpStatus.NOT_FOUND));

    }
}
