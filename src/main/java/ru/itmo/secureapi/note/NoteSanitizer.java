package ru.itmo.secureapi.note;

import java.text.Normalizer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

@Component
public class NoteSanitizer {

    private static final PolicyFactory PLAIN_TEXT_POLICY = new HtmlPolicyBuilder().toFactory();

    public String sanitize(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Note text must contain visible text");
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKC)
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .strip();
        String sanitized = PLAIN_TEXT_POLICY.sanitize(normalized).strip();
        if (sanitized.isBlank()) {
            throw new IllegalArgumentException("Note text must contain visible text");
        }
        return sanitized;
    }
}
