package edu.wustl.clinportal.util.global;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.ApplicationProperties;

public class EmailUtility 
{
	public static void sendmail(String body, String email, String sub ) throws BizLogicException
	{
		String HOST = "smtp.gmail.com";
	    String USER = "gogreen@paperfreedoctor.com";
	    String PASSWORD = "avani123";
	    String PORT = "465";
	    String FROM = "gogreen@paperfreedoctor.com";
	    String TO = email;

	    String STARTTLS = "true";
	    String AUTH = "true";
	    String DEBUG = "true";
	    String SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
	    String SUBJECT = sub;
	    //Use Properties object to set environment properties
	    String TEXT = body;
        
		Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.user", USER);

        props.put("mail.smtp.auth", AUTH);
        props.put("mail.smtp.starttls.enable", STARTTLS);
        props.put("mail.smtp.debug", DEBUG);

        props.put("mail.smtp.socketFactory.port", PORT);
        props.put("mail.smtp.socketFactory.class", SOCKET_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");
        try
        {
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(true);

            //Construct the mail message
            MimeMessage message = new MimeMessage(session);
            message.setText(TEXT);
            message.setSubject(SUBJECT);
            message.setFrom(new InternetAddress(FROM));
            message.addRecipient(RecipientType.TO, new InternetAddress(TO));
            message.saveChanges();

            //Use Transport to deliver the message
            Transport transport = session.getTransport("smtp");
            transport.connect(HOST, USER, PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        }
        catch (Exception e)
        {
	       e.printStackTrace();
	       throw new BizLogicException(null, null, "Email not set to participant");
	    }
	    
	}
	
	/**
	 * Returns the users details to be incorporated in the email.
	 * @param parti The user object.
	 * @return the users details to be incorporated in the email.
	 */
	public static String getParticipantDetailsEmailBody(edu.wustl.clinportal.domain.Participant parti, String hospRegId)
	{
		StringBuffer userDetailsBody = new StringBuffer();
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.crn")
				+ Constants.SEPARATOR + hospRegId);
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.fn")
				+ Constants.SEPARATOR + parti.getFirstName());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.ln")
				+ Constants.SEPARATOR + parti.getLastName());
		if(parti.getBloodGroup().equals("--Select--"))
		{
			userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.bg")
					+ Constants.SEPARATOR + "NA");
		}
		else
		{
			userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.bg")
					+ Constants.SEPARATOR + parti.getBloodGroup());
		}
		
		if(parti.getBusinessField().equals("--Select--"))
		{
			userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.emp")
					+ Constants.SEPARATOR + "NA");
			
		}
		else
		{
			userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.emp")
					+ Constants.SEPARATOR + parti.getBusinessField());
			
		}
		
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.gender")
				+ Constants.SEPARATOR + parti.getGender());
		if(parti.getBirthDate()==null)
		{
			userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.bd")
					+ Constants.SEPARATOR + "NA");
		}
		else
		{
			Integer bData = parti.getBirthDate().getDate();
			userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.bd")
				+ Constants.SEPARATOR + bData);
		}
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.medi")
				+ Constants.SEPARATOR + parti.getHealthInsurance());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.add")
				+ Constants.SEPARATOR + parti.getAddress().getStreet());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.phone")
				+ Constants.SEPARATOR + parti.getEmgContactNo());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.emg")
				+ Constants.SEPARATOR + parti.getEmgContactNo());


		return userDetailsBody.toString();
	}

}
