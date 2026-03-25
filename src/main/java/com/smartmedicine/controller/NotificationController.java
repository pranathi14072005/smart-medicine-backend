package com.smartmedicine.controller;

import com.smartmedicine.dto.ApiResponse;
import com.smartmedicine.model.User;
import com.smartmedicine.repository.UserRepository;
import com.smartmedicine.service.AuthService;
import com.smartmedicine.service.EmailService;
import com.smartmedicine.repository.ReminderRepository;
import com.smartmedicine.model.Reminder;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.Map;

/**
 * Endpoint for registering FCM device tokens so the backend can send
 * mobile / browser push notifications for medicine reminders.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Push notification management")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ReminderRepository reminderRepository;

    public NotificationController(AuthService authService, UserRepository userRepository, EmailService emailService, ReminderRepository reminderRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.reminderRepository = reminderRepository;
    }

    /**
     * Saves (or updates) the FCM registration token for the currently authenticated user.
     * The frontend calls this once after requesting notification permission.
     *
     * Body: { "token": "<fcm-registration-token>" }
     */
    @PostMapping("/token")
    @Operation(summary = "Register FCM device token")
    public ResponseEntity<ApiResponse<Void>> saveToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("FCM token must not be blank"));
        }
        User currentUser = authService.getCurrentUser();
        currentUser.setFcmToken(token);
        userRepository.save(currentUser);
        return ResponseEntity.ok(ApiResponse.success("FCM token registered", null));
    }

    /**
     * Removes the FCM token for the current user (e.g. on logout or when
     * the user denies notification permission).
     */
    @DeleteMapping("/token")
    @Operation(summary = "Remove FCM device token")
    public ResponseEntity<ApiResponse<Void>> removeToken() {
        User currentUser = authService.getCurrentUser();
        currentUser.setFcmToken(null);
        userRepository.save(currentUser);
        return ResponseEntity.ok(ApiResponse.success("FCM token removed", null));
    }

    @PostMapping("/test-email")
    public ResponseEntity<ApiResponse<String>> testEmail(@AuthenticationPrincipal User user) {
        if (user == null) {
            user = authService.getCurrentUser();
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User has no email configured"));
        }
        emailService.sendReminderEmail(user.getEmail(), user.getUsername(), "Test Medicine", "This is a diagnostic test email.");
        return ResponseEntity.ok(ApiResponse.success("Test email triggered for: " + user.getEmail()));
    }

    @GetMapping("/debug/reminders")
    public ResponseEntity<ApiResponse<List<Reminder>>> debugReminders() {
        User user = authService.getCurrentUser();
        List<Reminder> reminders = reminderRepository.findByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success("User Reminders Found: " + reminders.size(), reminders));
    }
}
