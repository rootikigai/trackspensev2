package ng.ikigai.trackspensev2.controller;

import lombok.RequiredArgsConstructor;
import ng.ikigai.trackspensev2.dto.AuthDTO;
import ng.ikigai.trackspensev2.dto.ProfileDTO;
import ng.ikigai.trackspensev2.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivated = profileService.activateProfile(token);
        if(isActivated){
            return ResponseEntity.ok("Success! Profile active.");
        }
        else{
            throw new RuntimeException("Activation token not found or already used!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
//        TODO: Re-enable this check when email activation is required again
        /*
        if(!profileService.isAccountActive(authDTO.getEmail())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", "Account is not active. Kindly check your email and activate your account first!"
            ));
        }
        */
        Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
        return ResponseEntity.ok(response);
    }
}
