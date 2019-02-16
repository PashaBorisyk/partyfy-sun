package services

import javax.inject.Inject
import org.apache.commons.mail.EmailAttachment
import play.api.libs.mailer._

class MailerService @Inject() (mailerClient: MailerClient) {
   
   def sendEmail = {
      val cid = "1234"
      val email = Email(
         "Simple email",
         "Mister FROM <borycompanyltd@gmail.com>",
         Seq("Miss TO <pashaborisyk@gmail.com>"),
         // adds attachment
         attachments = Seq(
            // adds inline attachment from byte array
            AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment
               .INLINE))
            // adds cid attachment
         ),
         // sends text, HTML or both...
         bodyText = Some("A text message"),
         bodyHtml = Some(s"""<html><body><p>An <b>html</b> message with cid <img src="cid:$cid"></p></body></html>""")
      )
      mailerClient.send(email)
   }
   
}
