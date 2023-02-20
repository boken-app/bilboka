package bilboka.messenger.resource

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("privacy")
class PrivacyPolicyResource {

    companion object {
        const val privacyPolicy = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<title>Data Privacy Policy</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<h1>Data Privacy Policy</h1>\n" +
                "\n" +
                "\t<p>At Bilboka, we are committed to protecting the privacy of our users. This data privacy policy explains how we collect, use, and protect the information you provide us when you use our message bot.</p>\n" +
                "\n" +
                "\t<h2>1. What information do we collect?</h2>\n" +
                "\n" +
                "\t<p>Our message bot only receives messages from users and does not collect any other information about users.</p>\n" +
                "\n" +
                "\t<h2>2. How do we use the information?</h2>\n" +
                "\n" +
                "\t<p>We only use the messages you send us to provide you with the service you requested. We do not use this information for any other purpose.</p>\n" +
                "\n" +
                "\t<h2>3. Do we share the information with third parties?</h2>\n" +
                "\n" +
                "\t<p>No, we do not share any information you provide us with any third parties.</p>\n" +
                "\n" +
                "\t<h2>4. How do we protect the information?</h2>\n" +
                "\n" +
                "\t<p>We take appropriate measures to protect the information you provide us. We use secure technologies to ensure that your messages are safe and cannot be accessed by unauthorized individuals.</p>\n" +
                "\n" +
                "\t<h2>5. Your rights</h2>\n" +
                "\n" +
                "\t<p>You have the right to access, rectify, or delete the information we hold about you. If you wish to exercise any of these rights, please contact us.</p>\n" +
                "\n" +
                "\t<h2>6. Changes to this data privacy policy</h2>\n" +
                "\n" +
                "\t<p>We may update this data privacy policy from time to time to reflect changes to our practices. We will notify you of any changes by posting the updated policy on our website.</p>\n" +
                "\n" +
                "\t<h2>7. Contact us</h2>\n" +
                "\n" +
                "\t<p>If you have any questions about this data privacy policy, please contact us.</p>\n" +
                "</body>\n" +
                "</html>\n"
    }

    @GetMapping
    fun get(): ResponseEntity<String> {
        return ResponseEntity.ok(privacyPolicy)
    }
}
