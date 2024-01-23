package ma.fstt.microserviceexaminateur.controllers;


import ma.fstt.microserviceexaminateur.entities.Examinater;
import ma.fstt.microserviceexaminateur.payload.request.AddExaminaterRequest;
import ma.fstt.microserviceexaminateur.payload.request.UpdateExaminaterRequest;
import ma.fstt.microserviceexaminateur.payload.response.MessageResponse;
import ma.fstt.microserviceexaminateur.repository.ExaminaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/examinaters")

public class ExaminaterController {

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    public ExaminaterRepository examinaterRepository;
    private final AuthService authService;
    public ExaminaterController(ExaminaterRepository examinaterRepository, AuthService authService) {
        this.examinaterRepository = examinaterRepository;
        this.authService = authService;
    }

    public String encodeDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return encoder.encode(sdf.format(date));
    }
    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addExaminater(@RequestBody AddExaminaterRequest addExaminaterRequest,@RequestHeader("Authorization") String authorizationHeader) {
        try {

            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
                if (!authService.isValidEmployeeToken(token)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
                }
                Examinater examinater = new Examinater(
                        addExaminaterRequest.getL_name(),
                        addExaminaterRequest.getF_name(),
                        addExaminaterRequest.getL_name()+"_"+addExaminaterRequest.getF_name(),
                        encodeDate(addExaminaterRequest.getDate_of_birth()),
                        addExaminaterRequest.getEmail(),
                        addExaminaterRequest.getPhone(),
                        addExaminaterRequest.getCity(),
                        addExaminaterRequest.getAddress(),
                        addExaminaterRequest.getCin(),
                        addExaminaterRequest.getDate_of_birth()
                );

                examinaterRepository.save(examinater);

                return ResponseEntity.ok(new MessageResponse(201,"Examinater saved successfully"));


        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(400,"Examinater doesn't save "));

        }
    }
    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllExaminaters(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }
            List<Examinater> examinaters = examinaterRepository.findAll();
            List<Examinater> filteredExaminaters = examinaters.stream()
                    .filter(examinater -> hasRole(examinater, "ROLE_EXAMINATER"))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(filteredExaminaters);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erreur interne du serveur
        }
    }
    private boolean hasRole(Examinater examinater, String role) {
        return examinater.getRole().equals(role);
    }

    @GetMapping("/{examinaterId}")
    public ResponseEntity<Object> getExaminaterById(@PathVariable String examinaterId,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }
            Optional<Examinater> examinater = examinaterRepository.findById(examinaterId);
            if (examinater.isPresent() &&  hasRole(examinater.get(), "ROLE_EXAMINATER")) {
                return ResponseEntity.ok(examinater.get());
            } else {
                return ResponseEntity.status(404).build(); // Ressource non trouvée
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Erreur interne du serveur
        }
    }


    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }


    @DeleteMapping("/{examinaterId}")
    public ResponseEntity<MessageResponse> deleteExaminater(@PathVariable String examinaterId,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }
            Optional<Examinater> examinater = examinaterRepository.findById(examinaterId);
            if (examinater.isPresent()) {
                examinaterRepository.delete(examinater.get());
                return ResponseEntity.ok(new MessageResponse(200, "Examinater deleted successfully"));
            } else {
                return ResponseEntity.status(404).body(new MessageResponse(404, "Examinater not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse(500, "Internal server error"));
        }
    }

    //update client
    @PutMapping("/{examinaterId}")
    public ResponseEntity<MessageResponse> examinaterUpdate(
            @PathVariable String examinaterId,
            @RequestBody UpdateExaminaterRequest updatedClientRequest,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            // Extract the token from the Authorization header
            String token = extractTokenFromHeader(authorizationHeader);
            if (!authService.isValidEmployeeToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(401, "Not authorized"));
            }
            Optional<Examinater> optionalExaminater = examinaterRepository.findById(examinaterId);
            if (optionalExaminater.isPresent()) {
                Examinater examinater = optionalExaminater.get();

                // Mettre à jour tous les champs de l'examinater
                examinater.setL_name(updatedClientRequest.getL_name());
                examinater.setF_name(updatedClientRequest.getF_name());
                examinater.setUsername(updatedClientRequest.getUsername());
                examinater.setEmail(updatedClientRequest.getEmail());
                examinater.setPhone(updatedClientRequest.getPhone());
                examinater.setCity(updatedClientRequest.getCity());
                examinater.setAddress(updatedClientRequest.getAddress());
                examinater.setCin(updatedClientRequest.getCin());
                examinater.setDate_of_birth(updatedClientRequest.getDate_of_birth());

                examinaterRepository.save(examinater);
                return ResponseEntity.ok(new MessageResponse(200, "Examinater updated successfully"));
            } else {
                return ResponseEntity.status(404).body(new MessageResponse(404, "Examinater not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse(500, "Internal server error"));
        }
    }

}