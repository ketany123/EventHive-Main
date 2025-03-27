package com.EventHive.controller;



import com.EventHive.entity.AttendEvent;
import com.EventHive.entity.Reservation;
import com.EventHive.repository.AttendEventRepository;
import com.EventHive.repository.ReservationRepository;
import com.EventHive.service.QrCodeService;
import com.EventHive.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/attendevent")
public class AttendEventController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AttendEventRepository attendeventRepository;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/uploadQRCode")
    public ResponseEntity<?> uploadQRCode(@RequestParam("file") MultipartFile file) throws Exception {
        BufferedImage uploadedImage = ImageIO.read(file.getInputStream());

        // Decode the uploaded QR Code image
        String qrCodeData = qrCodeService.decodeQRCode(uploadedImage);

        // Example of QR Code data: "UserId:12345"
//        String[] qrCodeParts = qrCodeData.split(":");

        // Validate QR code format
//        if (qrCodeParts.length < 2) {
//            return ResponseEntity.badRequest().body("Invalid QR Code format. Expected format: 'UserId:12345'");
//        }

        // Extract userId from the QR code data
        Long userId = null;
        try {
            userId = Long.parseLong(qrCodeData);  // Try to parse the userId part
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid UserId in QR Code: " );
        }

        // Validate the user
//        Reservation reservation = reservationRepository.findById(userId).orElse(null);
        Reservation reservation= reservationService.getReservationById(userId);
        if (reservation == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        if(reservation.isVisited()){
            return ResponseEntity.status(404).body("User attended already");
        }
        String username= reservation.getUser().getUsername();

        // Mark attendance
        AttendEvent attendance = new AttendEvent();
        attendance.setReservation(reservation);
        reservation.setVisited(true);

        attendeventRepository.save(attendance);

        return ResponseEntity.ok("Attendance marked for " + reservation.getId() + " user id" + username);
    }

}

