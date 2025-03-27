package com.EventHive.controller;


import com.EventHive.dto.ApiResponseDto;
import com.EventHive.dto.PagedApiResponseDto;
import com.EventHive.dto.ReservationRequestDto;
import com.EventHive.entity.Reservation;
import com.EventHive.repository.ReservationRepository;
import com.EventHive.service.QrCodeService;
import com.EventHive.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Autowired
    private QrCodeService qrCodeService;



    @GetMapping("/user/all")
    public ResponseEntity<PagedApiResponseDto> getAllReservationsForCurrentUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @GetMapping("/filter")
    public ResponseEntity<PagedApiResponseDto> filterReservations(
            @RequestParam(required = false) long theaterId,
            @RequestParam(required = false) long movieId,
            @RequestParam(required = false) long userId,
            @RequestParam(defaultValue = "BOOKED") String reservationStatus,
            @RequestParam(required = false) String createdDate
    ){

        return null;
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponseDto> createReservation(
            @RequestBody ReservationRequestDto reservationRequestDto
    )throws  Exception{
        String currentUserName = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Reservation reservation = reservationService.createReservation(reservationRequestDto, currentUserName);




        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponseDto.builder()
                                .data(reservation)
                                .message("Reservation created with id: " + reservation.getId())
                                .build()
                );
    }

    @GetMapping("/generateQRCode/{userId}")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Long userId) throws Exception {
//        Reservation reservation = ReservationRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Reservation reservation= reservationService.getReservationById(userId);
        // Check if the user has a QR code image
        byte[] qrCodeImage = reservation.getQrImage();
        if (qrCodeImage == null) {
            return ResponseEntity.status(404).body(null);  // Return 404 if QR code image is not found
        }

        return ResponseEntity.ok(qrCodeImage);
    }

    @GetMapping("/decode")
    public String decode(@RequestParam("file") MultipartFile file)throws Exception
    {
        BufferedImage uploadedImage = ImageIO.read(file.getInputStream());

        // Decode the uploaded QR Code image
        String qrCodeData = qrCodeService.decodeQRCode(uploadedImage);
        return qrCodeData;
    }




    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ApiResponseDto> getReservationById(@PathVariable long reservationId){
        Reservation reservation = reservationService.getReservationById(reservationId);
        return ResponseEntity.ok(
                ApiResponseDto.builder()
                        .message("Reservation Fetched with id: " + reservation.getId())
                        .data(reservation)
                        .build()
        );
    }

    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<ApiResponseDto> cancelReservation(@PathVariable long reservationId){
        Reservation reservation = reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok(
                ApiResponseDto.builder()
                        .message("Reservation Canceled")
                        .data(reservation)
                        .build()
        );
    }

}
