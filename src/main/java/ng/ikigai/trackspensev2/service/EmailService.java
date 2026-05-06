package ng.ikigai.trackspensev2.service;

import ng.ikigai.trackspensev2.exception.EmailSendingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {
    private String apikey;
    private String senderEmail;
    private String senderName;
    private final RestTemplate restTemplate = new RestTemplate();

    public EmailService(
            @Value("${brevo.api.key}") String apiKey,
            @Value("${brevo.sender.email}") String senderEmail,
            @Value("${brevo.sender.name}") String senderName
    ){
        this.apikey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void sendEmail(String to, String subject, String body){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apikey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", senderName, "email", senderEmail),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", body
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity("https://api.brevo.com/v3/smtp/email", request, String.class);

        } catch (Exception e){
            throw new EmailSendingException("Failed to send activation email to " + to + ". Please check your email address and try again.", e);
        }
    }
}
