/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/**
 * <p>Title: EmailHandler Class>
 * <p>Description:  EmailHandler is used to send emails during user signup, creation, forgot password.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.clinportal.util;

import javax.mail.MessagingException;

import edu.wustl.clinportal.domain.ReportedProblem;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.clinportal.util.global.Variables;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.EmailDetails;
import edu.wustl.common.util.global.SendEmail;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.SecurityManagerFactory;

/**
 * EmailHandler is used to send emails during user signup, creation, forgot password.
 * @author gautam_shetty
 */
public class EmailHandler
{

	private static final String EMAIL_SERVER = "email.mailServer";
	private static final String EMAIL_FROM_ADDR = "email.sendEmailFrom.emailAddress";
	private static final String REGARDS_CLINPORT = "email.catissuecore.team";
	private static final String REGARDS_CBMI = "email.cbmi.team";
	private static final String CATISSUE_URL = "catissue.url";
	private static final String CIDER_URL = "CIDER.url";

	/**
	 * Creates and sends the user registration approval email to user and the administrator.
	 * @param user The user whose registration is approved.
	 * @throws BizLogicException 
	 */
	public void sendApprovalEmail(User user) throws BizLogicException
	{
		String subject = ApplicationProperties.getValue("userRegistration.approve.subject");
		StringBuffer body = new StringBuffer();
		body.append("Dear " + user.getLastName() + "," + user.getFirstName());
		body.append("\n\n" + ApplicationProperties.getValue("userRegistration.approved.body.start")
				+ getUserDetailsEmailBody(user)); // Get the user details in the body of the email.

		//Send login details email to the user.
		sendLoginDetailsEmail(user, body.toString());

		body.append("\n\n" + ApplicationProperties.getValue("userRegistration.thank.body.end")
				+ "\n\n" + ApplicationProperties.getValue(REGARDS_CLINPORT));

		//Send the user registration details email to the administrator.
		boolean emailStatus;
		try
		{
			emailStatus = sendEmailToAdministrator(subject, body.toString());
		}
		catch (MessagingException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.emailHandler"), e,
					"Error sending login details email to admin");
		}

		if (emailStatus)
		{
			Logger.out.info(ApplicationProperties.getValue("user.approve.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			Logger.out.info(ApplicationProperties.getValue("user.approve.email.failure")
					+ user.getLastName() + " " + user.getFirstName());
		}
	}

	/**
	 * Returns the users details to be incorporated in the email.
	 * @param user The user object.
	 * @return the users details to be incorporated in the email.
	 */
	private String getUserDetailsEmailBody(User user)
	{
		StringBuffer userDetailsBody = new StringBuffer();
		userDetailsBody.append("\n\n" + ApplicationProperties.getValue("user.loginName")
				+ Constants.SEPARATOR + user.getLoginName());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.lastName")
				+ Constants.SEPARATOR + user.getLastName());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.firstName")
				+ Constants.SEPARATOR + user.getFirstName());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.street")
				+ Constants.SEPARATOR + user.getAddress().getStreet());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.city")
				+ Constants.SEPARATOR + user.getAddress().getCity());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.zipCode")
				+ Constants.SEPARATOR + user.getAddress().getZipCode());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.state")
				+ Constants.SEPARATOR + user.getAddress().getState());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.country")
				+ Constants.SEPARATOR + user.getAddress().getCountry());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.phoneNumber")
				+ Constants.SEPARATOR + user.getAddress().getPhoneNumber());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.faxNumber")
				+ Constants.SEPARATOR + user.getAddress().getFaxNumber());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.emailAddress")
				+ Constants.SEPARATOR + user.getEmailAddress());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.institution")
				+ Constants.SEPARATOR + user.getInstitution().getName());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.department")
				+ Constants.SEPARATOR + user.getDepartment().getName());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("user.cancerResearchGroup")
				+ Constants.SEPARATOR + user.getCancerResearchGroup().getName());

		return userDetailsBody.toString();
	}
	
	/**
	 * Returns the users details to be incorporated in the email.
	 * @param parti The user object.
	 * @return the users details to be incorporated in the email.
	 */
	private String getParticipantDetailsEmailBody(edu.wustl.clinportal.domain.Participant parti)
	{
		StringBuffer userDetailsBody = new StringBuffer();
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.crn")
				+ Constants.SEPARATOR + parti.getLastName());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.fn")
				+ Constants.SEPARATOR + parti.getFirstName());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.ln")
				+ Constants.SEPARATOR + parti.getLastName());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.emp")
				+ Constants.SEPARATOR + parti.getBusinessField());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.bg")
				+ Constants.SEPARATOR + parti.getBloodGroup());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.gender")
				+ Constants.SEPARATOR + parti.getGender());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.bd")
				+ Constants.SEPARATOR + parti.getBirthDate());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.medi")
				+ Constants.SEPARATOR + parti.getHealthInsurance());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.add")
				+ Constants.SEPARATOR + parti.getAddress());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.phone")
				+ Constants.SEPARATOR + parti.getEmgContactNo());
		userDetailsBody.append("\n" + ApplicationProperties.getValue("participant.emg")
				+ Constants.SEPARATOR + parti.getEmgContactNo());


		return userDetailsBody.toString();
	}

	/**
	 * Creates and sends the user registration rejection emails to user and the administrator.
	 * @param user The user whose registration is rejected.
	 * @throws BizLogicException 
	 */
	public void sendRejectionEmail(User user) throws BizLogicException
	{
		String subject = ApplicationProperties.getValue("userRegistration.reject.subject");
		StringBuffer body = new StringBuffer();
		body.append("Dear " + user.getLastName() + "," + user.getFirstName() + "\n\n"
				+ ApplicationProperties.getValue("userRegistration.reject.body.start"));

		//Append the comments given by the administrator, if any.
		if ((user.getComments() != null) && ("".equals(user.getComments()) == false))
		{
			body.append("\n\n" + ApplicationProperties.getValue("userRegistration.reject.comments")
					+ user.getComments());
		}

		body = body.append("\n\n"
				+ ApplicationProperties.getValue("userRegistration.thank.body.end") + "\n\n"
				+ ApplicationProperties.getValue(REGARDS_CLINPORT));

		boolean emailStatus = sendEmailToUserAndAdministrator(user.getEmailAddress(), subject, body
				.toString(), true);

		if (emailStatus)
		{
			Logger.out.info(ApplicationProperties.getValue("user.reject.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			Logger.out.info(ApplicationProperties.getValue("user.reject.email.failure")
					+ user.getLastName() + " " + user.getFirstName());
		}
	}

	/**
	 * Creates and sends the user signup request received email to the user and the administrator.
	 * @param user The user registered for the membership.
	 * @throws BizLogicException 
	 */
	public void sendUserSignUpEmail(User user) throws BizLogicException
	{
		String subject = ApplicationProperties.getValue("userRegistration.request.subject");

		String body = "Dear " + user.getLastName() + "," + user.getFirstName() + "\n\n"
				+ ApplicationProperties.getValue("userRegistration.request.body.start") + "\n"
				+ getUserDetailsEmailBody(user) + "\n\n\t"
				+ ApplicationProperties.getValue("userRegistration.request.body.end") + "\n\n"
				+ ApplicationProperties.getValue(REGARDS_CLINPORT);

		boolean emailStatus = sendEmailToUserAndAdministrator(user.getEmailAddress(), subject,
				body, true);

		if (emailStatus)
		{
			Logger.out.info(ApplicationProperties.getValue("userRegistration.email.success")
					+ user.getLastName() + " " + user.getFirstName());
		}
		else
		{
			Logger.out.info(ApplicationProperties.getValue("userRegistration.email.failure")
					+ user.getLastName() + " " + user.getFirstName());
		}
	}

	/**
	 * Creates and sends the login details email to the user.
	 * Returns true if the email is successfully sent else returns false.
	 * @param user The user whose login details are to be sent. 
	 * @param userDetailsBody User registration details.
	 * @return true if the email is successfully sent else returns false.
	 * @throws BizLogicException 
	 */
	public boolean sendLoginDetailsEmail(User user, String userDetailsBody)
			throws BizLogicException
	{
		boolean emailStatus = false;

		try
		{
			String subject = ApplicationProperties.getValue("loginDetails.email.subject");
			StringBuffer body = new StringBuffer();
			body.append("Dear " + user.getFirstName() + " " + user.getLastName());

			if (userDetailsBody != null)
			{
				body = new StringBuffer(userDetailsBody);
			}

			gov.nih.nci.security.authorization.domainobjects.User csmUser = SecurityManagerFactory
					.getSecurityManager().getUserById(user.getCsmUserId().toString());

			//			String roleOfUser = SecurityManagerFactory.getSecurityManager().getUserRole(
			//					user.getCsmUserId().longValue()).getName();
			body.append("\n\n" + ApplicationProperties.getValue("forgotPassword.email.body.start"));
			body.append("\n\t " + ApplicationProperties.getValue("user.loginName")
					+ Constants.SEPARATOR + user.getLoginName());
			body.append("\n\t " + ApplicationProperties.getValue("user.password")
					+ Constants.SEPARATOR + csmUser.getPassword());
			//			body.append("\n\t " + ApplicationProperties.getValue("user.role") + Constants.SEPARATOR
			//					+ roleOfUser);
			//	body.append("\n\n" + ApplicationProperties.getValue(REGARDS_CLINPORT));

			emailStatus = sendEmailToUser(user.getEmailAddress(), subject, body.toString());

			if (emailStatus)
			{
				Logger.out.info(ApplicationProperties.getValue("user.loginDetails.email.success")
						+ user.getLastName() + " " + user.getFirstName());
			}
			else
			{
				Logger.out.info(ApplicationProperties.getValue("user.loginDetails.email.failure")
						+ user.getLastName() + " " + user.getFirstName());
			}
		}
		catch (SMException smExp)
		{
			//throw new DAOException(smExp.getMessage(), smExp);
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.emailHandler"), smExp,
					"Error in sending login details email");
		}
		catch (MessagingException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.emailHandler"), e,
					"Error in sending login details email");
		}

		return emailStatus;
	}

	/**
	 * Sends email to the administrator and the user who reported the problem.
	 * @param reportedProblem The problem reported.
	 * @throws BizLogicException 
	 */
	public void sendReportedProblemEmail(ReportedProblem reportedProblem) throws BizLogicException
	{
		// Send the reported problem to administrator and the user who reported it.
		String body = ApplicationProperties.getValue("email.reportProblem.body.start") + "\n "
				+ ApplicationProperties.getValue("reportedProblem.from") + " : "
				+ reportedProblem.getFrom() + "\n "
				+ ApplicationProperties.getValue("reportedProblem.title") + " : "
				+ reportedProblem.getSubject() + "\n "
				+ ApplicationProperties.getValue("reportedProblem.message") + " : "
				+ reportedProblem.getMessageBody() + "\n\n"
				+ ApplicationProperties.getValue(REGARDS_CLINPORT);

		String subject = ApplicationProperties.getValue("email.reportProblem.subject");

		boolean emailStatus = sendEmailToUserAndAdministrator(reportedProblem.getFrom(), subject,
				body, false);

		if (emailStatus)
		{
			Logger.out.info(ApplicationProperties.getValue("reportedProblem.email.success"));
		}
		else
		{
			Logger.out.info(ApplicationProperties.getValue("reportedProblem.email.failure"));
		}
	}

	/**
	 * Sends email to the user with the email address passed.
	 * Returns true if the email is successfully sent else returns false.
	 * @param userEmailAddress Email address of the user.
	 * @param subject The subject of the email. 
	 * @param body The body of the email.
	 * @return true if the email is successfully sent else returns false.
	 * @throws MessagingException 
	 */
	private boolean sendEmailToUser(String userEmailAddress, String subject, String body)
			throws MessagingException
	{
		String mailServer = XMLPropertyHandler.getValue(EMAIL_SERVER);
		String sendFromEmailId = XMLPropertyHandler.getValue(EMAIL_FROM_ADDR);
		String caTissueUrl = getURL(XMLPropertyHandler.getValue(CATISSUE_URL));
		String CIDERUrl = getURL(XMLPropertyHandler.getValue(CIDER_URL));
		Variables.catissueURL = CommonServiceLocator.getInstance().getAppURL();
		StringBuffer bodyValue = new StringBuffer();
		bodyValue.append(
				body + "\n\n" + ApplicationProperties.getValue("loginDetails.catissue.url.message")
						+ "\n\n").append(ApplicationProperties.getValue("app.name")).append(
				Constants.SEPARATOR).append(getURL(Variables.catissueURL));
		if (caTissueUrl != null && CIDERUrl != null)
			bodyValue.append("\n").append(ApplicationProperties.getValue("catissue.name")).append(
					Constants.SEPARATOR).append(caTissueUrl).append("\n").append(
					ApplicationProperties.getValue("cider.name")).append(Constants.SEPARATOR)
					.append(CIDERUrl);
		bodyValue.append("\n\n").append(
				ApplicationProperties.getValue("userRegistration.contact.details"));
		bodyValue.append("\n\n" + ApplicationProperties.getValue(REGARDS_CBMI));
		/*SendEmail email = new SendEmail();
		return email.sendmail(userEmailAddress, sendFromEmailId, mailServer, subject, bodyValue
			.toString());*/
		EmailDetails emailDetails = new EmailDetails();
		emailDetails.addToAddress(userEmailAddress);
		emailDetails.setSubject(subject);
		emailDetails.setBody(bodyValue.toString());

		SendEmail email = new SendEmail(mailServer, sendFromEmailId);
		return email.sendMail(emailDetails);

	}
	
	/**
	 * Creates and sends the user signup request received email to the user and the administrator.
	 * @param user The user registered for the membership.
	 * @throws BizLogicException 
	 */
	public void sendParticipantRegEmail(edu.wustl.clinportal.domain.Participant participant,
			String PI, String shortTitle) throws BizLogicException
	{
		String thanks = "Thanks," + PI;
		String subject = ApplicationProperties.getValue("participantRegistration.request.subject") +" "+ shortTitle;
		String body = "Dear " + participant.getLastName() + "," + participant.getFirstName() + "\n\n"
				+ ApplicationProperties.getValue("participantDetails") + "\n\n"
				+ getParticipantDetailsEmailBody(participant)+"\n\n\t"
				+ thanks +"\n\n"
				+ ApplicationProperties.getValue("participantenddetails");
		boolean emailStatus = sendEmailToParticipantAndAdministrator(participant.getEmailAddress(), subject,
				body, true);
		if (emailStatus)
		{
			Logger.out.info(ApplicationProperties.getValue("userRegistration.email.success")
					+ participant.getLastName() + " " + participant.getFirstName());
		}
		else
		{
			Logger.out.info(ApplicationProperties.getValue("userRegistration.email.failure")
					+ participant.getLastName() + " " + participant.getFirstName());
		}
	}

	/**
	 * Sends email to the administrator and user with the email address passed.
	 * Returns true if the email is successfully sent else returns false.
	 * @param userEmailAddress Email address of the user.
	 * @param subject The subject of the email. 
	 * @param body The body of the email.
	 * @param toUser
	 * @return true if the email is successfully sent else returns false.
	 * @throws BizLogicException 
	 */
	private boolean sendEmailToUserAndAdministrator(String userEmailAddress, String subject,
			String body, boolean toUser) throws BizLogicException
	{
		String adminEmailAddress = XMLPropertyHandler.getValue("email.administrative.emailAddress");
		String sendFromEmailId = XMLPropertyHandler.getValue(EMAIL_FROM_ADDR);
		String mailServer = XMLPropertyHandler.getValue(EMAIL_SERVER);
		Variables.catissueURL = CommonServiceLocator.getInstance().getAppURL();
		StringBuffer bodyValue = new StringBuffer();

		bodyValue.append(body);
		bodyValue.append("\n\n");
		if (!subject.equals(ApplicationProperties.getValue("userRegistration.reject.subject")))
		{
			bodyValue.append(ApplicationProperties.getValue("loginDetails.catissue.url.message"));
			bodyValue.append(Variables.catissueURL);
		}

		/*SendEmail email = new SendEmail();
		return email.sendmail(userEmailAddress, adminEmailAddress, null, sendFromEmailId,
				mailServer, subject, bodyValue.toString());*/

		EmailDetails emailDetails = new EmailDetails();

		if (toUser)
		{
			emailDetails.addToAddress(userEmailAddress);
			emailDetails.addCcAddress(adminEmailAddress);
		}
		else
		{
			emailDetails.addToAddress(adminEmailAddress);
			emailDetails.addCcAddress(userEmailAddress);
		}
		emailDetails.setSubject(subject);
		emailDetails.setBody(bodyValue.toString());

		SendEmail email;
		try
		{
			email = new SendEmail(mailServer, sendFromEmailId);
		}
		catch (MessagingException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.emailHandler"), e,
					"Error in email setup");
		}
		return email.sendMail(emailDetails);
	}
	/**
	 * Sends email to the administrator and user with the email address passed.
	 * Returns true if the email is successfully sent else returns false.
	 * @param userEmailAddress Email address of the user.
	 * @param subject The subject of the email. 
	 * @param body The body of the email.
	 * @param toUser
	 * @return true if the email is successfully sent else returns false.
	 * @throws BizLogicException 
	 */
	private boolean sendEmailToParticipantAndAdministrator(String userEmailAddress, String subject,
			String body, boolean toUser) throws BizLogicException
	{
		String adminEmailAddress = XMLPropertyHandler.getValue("email.administrative.emailAddress");
		String sendFromEmailId = XMLPropertyHandler.getValue(EMAIL_FROM_ADDR);
		String mailServer = XMLPropertyHandler.getValue(EMAIL_SERVER);

		StringBuffer bodyValue = new StringBuffer();
		bodyValue.append(body);
		bodyValue.append("\n\n");

		EmailDetails emailDetails = new EmailDetails();

		if (toUser)
		{
			emailDetails.addToAddress(userEmailAddress);
			emailDetails.addCcAddress(adminEmailAddress);
		}
		else
		{
			emailDetails.addToAddress(adminEmailAddress);
			emailDetails.addCcAddress(userEmailAddress);
		}
		emailDetails.setSubject(subject);
		emailDetails.setBody(bodyValue.toString());

		SendEmail email;
		try
		{
			email = new SendEmail(mailServer, sendFromEmailId);
		}
		catch (MessagingException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.emailHandler"), e,
					"Error in email setup");
		}
		return email.sendMail(emailDetails);
	}

	/**
	 * Sends email to the administrator.
	 * Returns true if the email is successfully sent else returns false.
	 * @param subject The subject of the email. 
	 * @param body The body of the email.
	 * @return true if the email is successfully sent else returns false.
	 * @throws MessagingException 
	 */
	private boolean sendEmailToAdministrator(String subject, String body) throws MessagingException
	{
		StringBuffer bodyValue = new StringBuffer();
		String adminEmailAddress = XMLPropertyHandler.getValue("email.administrative.emailAddress");
		String sendFromEmailId = XMLPropertyHandler.getValue(EMAIL_FROM_ADDR);
		String mailServer = XMLPropertyHandler.getValue(EMAIL_SERVER);
		String caTissueUrl = getURL(XMLPropertyHandler.getValue(CATISSUE_URL));
		String CIDERUrl = getURL(XMLPropertyHandler.getValue(CIDER_URL));
		Variables.catissueURL = CommonServiceLocator.getInstance().getAppURL();
		bodyValue.append(
				body + "\n\n" + ApplicationProperties.getValue("loginDetails.catissue.url.message")
						+ "\n\n").append(ApplicationProperties.getValue("app.name")).append(
				Constants.SEPARATOR).append(getURL(Variables.catissueURL));
		if (caTissueUrl != null && CIDERUrl != null)
			bodyValue.append("\n").append(ApplicationProperties.getValue("catissue.name")).append(
					Constants.SEPARATOR).append(caTissueUrl).append("\n").append(
					ApplicationProperties.getValue("cider.name")).append(Constants.SEPARATOR)
					.append(CIDERUrl);
		bodyValue.append("\n\n").append(
				ApplicationProperties.getValue("userRegistration.contact.details"));
		bodyValue.append("\n\n" + ApplicationProperties.getValue(REGARDS_CBMI));
		/*SendEmail email = new SendEmail();
		boolean emailStatus = email.sendmail(adminEmailAddress, sendFromEmailId, mailServer,
				subject, bodyValue.toString());*/

		EmailDetails emailDetails = new EmailDetails();
		emailDetails.addToAddress(adminEmailAddress);
		emailDetails.setSubject(subject);
		emailDetails.setBody(bodyValue.toString());

		SendEmail email = new SendEmail(mailServer, sendFromEmailId);
		return email.sendMail(emailDetails);
	}

	/**
	 * 
	 * @param requestURL
	 * @return
	 */
	private String getURL(String requestURL)
	{
		String ourUrl = "";
		ourUrl = requestURL + "\r";
		return ourUrl;
	}

	/**
	 * Sends email to Administrator and CC to Scientist on successful placement of order.
	 * Returns true if mail is sent successfully.
	 * @param ccEmailAddress
	 * @param emailBody
	 * @param subject
	 * @return boolean indicating true/false
	 * @throws MessagingException 
	 */
	public boolean sendEmailForOrderingPlacement(String ccEmailAddress, String emailBody,
			String subject) throws MessagingException
	{
		String toEmailAddress = XMLPropertyHandler.getValue("email.administrative.emailAddress");
		String sendFrmEmailId = XMLPropertyHandler.getValue(EMAIL_FROM_ADDR);
		String mailServer = XMLPropertyHandler.getValue(EMAIL_SERVER);

		//		SendEmail email = new SendEmail();
		//		return email.sendmail(toEmailAddress, ccEmailAddress, null, sendFrmEmailId, mailServer,
		//				subject, emailBody);

		EmailDetails emailDetails = new EmailDetails();
		emailDetails.addToAddress(toEmailAddress);
		emailDetails.setSubject(subject);
		emailDetails.setBody(emailBody);

		SendEmail email = new SendEmail(mailServer, sendFrmEmailId);
		return email.sendMail(emailDetails);
	}

	/**
	 * Sends email to Scientist and cc to Admin on distribution of the order.
	 * @param body
	 * @param toEmailAddress
	 * @param fromEmailAddress
	 * @param subject
	 * @return
	 * @throws MessagingException 
	 */
	public boolean sendEmailForOrderDistribution(String body, String toEmailAddress,
			String fromEmailAddress, String subject) throws MessagingException
	{
		String mailServer = XMLPropertyHandler.getValue(EMAIL_SERVER);

		/*SendEmail email = new SendEmail();
		boolean emailStatus = email.sendmail(toEmailAddress, null, null, fromEmailAddress,
				mailServer, subject, body);*/

		EmailDetails emailDetails = new EmailDetails();
		emailDetails.addToAddress(toEmailAddress);
		emailDetails.setSubject(subject);
		emailDetails.setBody(body);

		SendEmail email = new SendEmail(mailServer, fromEmailAddress);
		return email.sendMail(emailDetails);
	}

	/**
	 * Creates and sends the user account closure email to the user.
	 * @param user The user whose account is closed.
	 * @throws BizLogicException 
	 */
	public void sendUserAccountClosureEmail(User user) throws BizLogicException
	{
		String subject = ApplicationProperties.getValue("userRegistration.closed.subject");
		StringBuffer body = new StringBuffer();
		body.append("Dear ");
		body.append(user.getLastName() + ",");
		body.append(user.getFirstName());
		body.append("\n\n");
		body.append(ApplicationProperties.getValue("userRegistration.closed.body.start"));

		//Append the comments given by the administrator, if any.
		if ((user.getComments() != null) && !("".equals(user.getComments())))
		{
			body.append("\n\n");
			body.append(ApplicationProperties.getValue("userRegistration.reject.comments"));
			body.append(user.getComments());
		}

		body.append("\n\n");
		body.append(ApplicationProperties.getValue("userRegistration.closed.thank.body.end"));
		body.append("\n\n");
		body.append(ApplicationProperties.getValue(REGARDS_CLINPORT));

		boolean emailStatus;
		try
		{
			emailStatus = sendEmailToUser(user.getEmailAddress(), subject, body.toString());

			if (emailStatus)
			{
				Logger.out.info(ApplicationProperties.getValue("user.closed.email.success")
						+ user.getLastName() + " " + user.getFirstName());
			}
			else
			{
				Logger.out.info(ApplicationProperties.getValue("user.closed.email.failure")
						+ user.getLastName() + " " + user.getFirstName());
			}
		}
		catch (MessagingException e)
		{
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.emailHandler"), e,
					"Error in sending user account closure mail");
		}
	}
	
}