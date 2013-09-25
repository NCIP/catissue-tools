/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.ParticipantMedicalIdentifier;
import edu.wustl.clinportal.domain.Race;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.lookup.DefaultLookupParameters;
import edu.wustl.common.lookup.DefaultLookupResult;
import edu.wustl.common.lookup.LookupLogic;
import edu.wustl.common.lookup.LookupParameters;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author falguni_sachde
 * 
 * This class is for finding out the matching participant with respect to given participant. 
 * It implements the lookUp method of LookupLogic interface which returns the list of all matching 
 * participants to the given participant. 
 * 
 */
public class ParticipantLookupLogic implements LookupLogic
{

	// Getting points from the xml file in static variables
	protected static int POINTSFORSSNEXACT;
	protected static int POINTSFORSSNPARTIAL;
	protected static int POINTSFORPMIEXACT;
	protected static int POINTSFORPMIPARTIAL;
	protected static int POINTSFORDOBEXACT;
	protected static int POINTSFORDOBPARTIAL;
	protected static int POINTSFORLASTNAMEEXACT;
	protected static int POINTSFORLASTNAMEPARTIAL;
	protected static int POINTSFORFIRSTNAMEEXACT;
	protected static int POINTSFORFIRSTNAMEPARTIAL;
	protected static int POINTSFORMIDDLENAMEEXACT;
	protected static int POINTSFORMIDDLENAMEPARTIAL;
	protected static int POINTSFORRACEEXACT;
	//private static int pointsForRacePartial = Integer.parseInt(XMLPropertyHandler.getValue(Constants.PARTICIPANT_RACE_PARTIAL));
	protected static int POINTSFORGENDEREXACT;
	protected static int BONUSPOINTS;
	protected static int MATCHCHARACTERSFORLASTNAME;
	protected static int CUTOFFPOINTSFROMPROPERTIES;
	protected static int TOTALPOINTSFROMPROPERTIES;
	protected int cutoffPoints;
	protected int totalPoints;
	protected boolean isSSNOrPMI = false;
	protected boolean exactMatch = true;

	/**
	 * This function first retrieves all the participants present in the PARTICIPANT table. Then it checks 
	 * for possible match of given participant and the list of participants retrieved from database. 
	 * Based on the criteria in the MPI matching algorithm, it returns the list of all matching participants. 
	 * @param params - LookupParameters
	 * @return list - List of matching Participants. 
	 */
	public List lookup(LookupParameters params) throws Exception
	{
		// Done for Junit
		initializeWeights();
		if (params == null)
		{
			throw new Exception("Params can not be null");
		}

		DefaultLookupParameters participantParams = (DefaultLookupParameters) params;

		// Getting the participant object created by user
		Participant participant = (Participant) participantParams.getObject();

		// if cutoff is greater than total points, throw exception
		if (cutoffPoints > TOTALPOINTSFROMPROPERTIES)
		{
			throw new Exception(ApplicationProperties.getValue("errors.lookup.cutoff"));
		}

		// get total points depending on Participant object created by user
		totalPoints = calculateTotalPoints(participant);

		// adjust cutoffPoints as per new total points 
		cutoffPoints = calculateCutOffPoints();
		List lstOfPartipants = new ArrayList();
		Map participantMap = participantParams.getListOfParticipants();
		lstOfPartipants.addAll(participantMap.values());
		// In case List of participants is null or empty, return the Matching Participant List as null.
		if (lstOfPartipants == null || lstOfPartipants.isEmpty() == true)
		{
			return null;
		}

		// calling the searchMatchingParticipant to filter the participant list according to given cutoff value
		List participants = searchMatchingParticipant(participant, lstOfPartipants);

		return participants;

	}

	/**
	 * @return cutoff points
	 */
	protected int calculateCutOffPoints()
	{
		int cutOffPoints = CUTOFFPOINTSFROMPROPERTIES * totalPoints / TOTALPOINTSFROMPROPERTIES;
		return cutOffPoints;
	}

	/**
	 * Initializes the weights from catissue_properties.xml file
	 */
	protected void initializeWeights()
	{
		POINTSFORSSNEXACT = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_SSN_EXACT));
		POINTSFORSSNPARTIAL = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_SSN_PARTIAL));
		POINTSFORPMIEXACT = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_PMI_EXACT));
		POINTSFORPMIPARTIAL = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_PMI_PARTIAL));
		POINTSFORDOBEXACT = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_DOB_EXACT));
		POINTSFORDOBPARTIAL = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_DOB_PARTIAL));
		POINTSFORLASTNAMEEXACT = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_LAST_NAME_EXACT));
		POINTSFORLASTNAMEPARTIAL = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_LAST_NAME_PARTIAL));
		POINTSFORFIRSTNAMEEXACT = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_FIRST_NAME_EXACT));
		POINTSFORFIRSTNAMEPARTIAL = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_FIRST_NAME_PARTIAL));
		POINTSFORMIDDLENAMEEXACT = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_MIDDLE_NAME_EXACT));
		POINTSFORMIDDLENAMEPARTIAL = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_MIDDLE_NAME_PARTIAL));
		POINTSFORRACEEXACT = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_RACE_EXACT));
		//private static int pointsForRacePartial = Integer.parseInt(XMLPropertyHandler.getValue(Constants.PARTICIPANT_RACE_PARTIAL));
		POINTSFORGENDEREXACT = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_GENDER_EXACT));
		BONUSPOINTS = Integer.parseInt(XMLPropertyHandler.getValue(Constants.PARTICIPANT_BONUS));
		MATCHCHARACTERSFORLASTNAME = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_MATCH_CHARACTERS_FOR_LAST_NAME));
		CUTOFFPOINTSFROMPROPERTIES = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.PARTICIPANT_LOOKUP_CUTOFF));
		TOTALPOINTSFROMPROPERTIES = POINTSFORFIRSTNAMEEXACT + POINTSFORMIDDLENAMEEXACT
				+ POINTSFORLASTNAMEEXACT + POINTSFORDOBEXACT + POINTSFORSSNEXACT
				+ POINTSFORGENDEREXACT + POINTSFORRACEEXACT;

	}

	/**
	 *  This function calculates the total based on values entered by user
	 * @param participant - participant object
	 * @return - cutoff points
	 */
	protected int calculateTotalPoints(Participant participant)
	{
		int totPointsForPpant = 0;
		if (participant.getBirthDate() != null)
		{
			totPointsForPpant += POINTSFORDOBEXACT;
		}
		if (participant.getFirstName() != null && !participant.getFirstName().trim().equals(""))
		{
			totPointsForPpant += POINTSFORFIRSTNAMEEXACT;
		}
		if (participant.getMiddleName() != null && !participant.getMiddleName().trim().equals(""))
		{
			totPointsForPpant += POINTSFORMIDDLENAMEEXACT;
		}
		if (participant.getLastName() != null && !participant.getLastName().trim().equals(""))
		{
			totPointsForPpant += POINTSFORLASTNAMEEXACT;
		}
		if (participant.getSocialSecurityNumber() != null
				&& !participant.getSocialSecurityNumber().trim().equals(""))
		{
			totPointsForPpant += POINTSFORSSNEXACT;
		}
        if (participant.getGender() != null && !participant.getGender().trim().equals("") 
                && (!participant.getGender().equals("Unspecified")))
        {
            totPointsForPpant += POINTSFORGENDEREXACT;
        }
        if (participant.getRaceCollection() != null && participant.getRaceCollection().isEmpty() == false)
        {
            Iterator<Race> raceIterator = participant.getRaceCollection().iterator();
            Race race = raceIterator.next();
            if (!race.getRaceName().equals("Unknown"))
            {
                totPointsForPpant += POINTSFORRACEEXACT;
            }
        }
		return totPointsForPpant;
	}

	/**
	 * This function searches the participant which has matching probability more than cutoff. The different criteria 
	 * considered for finding a possible match are
	 * 1. Social Security Number
	 * 2. Date Of Birth
	 * 3. Last Name 
	 * 4. Middle Name 
	 * 5. First Name 
	 * 6. Race
	 * 7. Gender
	 * 
	 * Points are given to complete or partial match of these parameters. If the total of all these points exceeds 
	 * the cut-off points, the participant is considered as a match for the given participant. List of all such 
	 * participants is returned by this function.  
	 * 
	 * @param userParticipant - participant with which comparison is to be done.
	 * @param lstOfPpants - List of all participants which has at least one matching parameter.
	 * @param cutoff - is the value such that the participants above the cutoff values are stored in List.
	 * @return list - List of matching Participants. 
	 */
	protected List searchMatchingParticipant(Participant userParticipant, List lstOfPpants)
			throws Exception
	{
		List participants = new ArrayList();
		Iterator itr = lstOfPpants.iterator();

		// Iterates through all the Participants from the list 
		while (itr.hasNext())
		{
			int weight = 0; // used for calculation of total points.
			int socialSecNoWght = 0; // points of social security number
			int birthDateWeight = 0; // points of birth date
			Participant existPpant = (Participant) itr.next();
			exactMatch = true;
			// Check for the participant only in case its Activity Status = active
			if (existPpant.getActivityStatus() != null
					&& existPpant.getActivityStatus().equals(Constants.ACTIVITY_STATUS_ACTIVE))
			{

				/**
				 *  If user has entered Social Security Number and it is present in the participant from database as well,
				 *  check for match between the two.
				 */
				if (userParticipant.getSocialSecurityNumber() != null
						&& !userParticipant.getSocialSecurityNumber().trim().equals("")
						&& existPpant.getSocialSecurityNumber() != null
						&& !existPpant.getSocialSecurityNumber().trim().equals(""))
				{
					socialSecNoWght = checkNumber(userParticipant.getSocialSecurityNumber().trim()
							.toLowerCase(), existPpant.getSocialSecurityNumber().trim()
							.toLowerCase(), true);
					weight = socialSecNoWght;
				}

				/**
				 *  If user has entered Date of Birth and it is present in the participant from database as well,
				 *  check for match between the two.
				 */
				if (userParticipant.getBirthDate() != null && existPpant.getBirthDate() != null)
				{
					birthDateWeight = checkDateOfBirth(userParticipant.getBirthDate(), existPpant
							.getBirthDate());
					weight += birthDateWeight;
				}

				/**
				 *  If user has entered Last Name and it is present in the participant from database as well,
				 *  check for match between the two.
				 */
				if (userParticipant.getLastName() != null
						&& !userParticipant.getLastName().trim().equals("")
						&& existPpant.getLastName() != null
						&& !existPpant.getLastName().trim().equals(""))
				{
					weight += checkLastName(userParticipant.getLastName().trim().toLowerCase(),
							existPpant.getLastName().trim().toLowerCase());

				}

				/**
				 *  If user has entered First Name and it is present in the participant from database as well,
				 *  check for match between the two.
				 */
				if (userParticipant.getFirstName() != null
						&& !userParticipant.getFirstName().trim().equals("")
						&& existPpant.getFirstName() != null
						&& !existPpant.getFirstName().trim().equals(""))
				{
					weight += checkFirstName(userParticipant.getFirstName().trim().toLowerCase(),
							existPpant.getFirstName().trim().toLowerCase());
				}

				/**
				 *  Bonus points are given in case user entered LastName, FirstName, DOB match completely with
				 *  the respective fields of participant from database.
				 */
				if (weight - socialSecNoWght == POINTSFORLASTNAMEEXACT + POINTSFORFIRSTNAMEEXACT
						+ POINTSFORDOBEXACT)
				{
					weight += BONUSPOINTS;
				}

				/**
				 * The first and last names are first parsed to determine if multiple names exist in either field separated by a space, dash or comma.  
				 * If so they are split and placed in the appropriate fields.  We also do a flip of the first and last names and try that because 
				 * sometimes people don’t enter them correctly (put last name in first name field and vice versa).
				 * This check is applied only when none of name or surname of user entered participant completely or partially matches with name and surname.
				 */

				if (weight == socialSecNoWght + birthDateWeight)
				{
					weight += checkFlipped(userParticipant.getFirstName(), userParticipant
							.getLastName(), existPpant.getFirstName(), existPpant.getLastName());
				}

				weight += checkParticipantMedicalIdentifier(userParticipant
						.getParticipantMedicalIdentifierCollection(), existPpant);

				/**
				 *  check whether weight will ever reach cutoff, if it will never reach the cutoff, skip this 
				 *  participant and take next one.
				 */
				int temp = cutoffPoints - POINTSFORMIDDLENAMEEXACT - POINTSFORRACEEXACT
						- POINTSFORGENDEREXACT;

				if (!isSSNOrPMI && weight < temp)
				{
					continue;
				}

				/**
				 *  check for possible match of middle name
				 */

				weight += checkMiddleName(userParticipant.getMiddleName(), existPpant
						.getMiddleName());

				/**
				 *  If user has Gender and it is present in the participant from database as well,
				 *  check for match between the two.
				 */
				if (userParticipant.getGender() != null && existPpant.getGender() != null)
				{
					weight += checkGender(userParticipant.getGender(), existPpant.getGender());
				}

				/**
				 *  If user has entered Race and it is present in the participant from database as well,
				 *  check for match between the two.
				 */
				weight += checkRace(userParticipant.getRaceCollection(), existPpant
						.getRaceCollection());

				/**
				 *  Description :If user has entered Medical Recoded No and it is present in the participant from database as well,
				 *  check for match between the two.
				 */

				// If total points are greater than cutoff points, add participant to the List
				updateParticipantList(participants, weight, existPpant);
				if (participants.size() == 100) // Return when matching participant list size becomes 100
				{
					return participants;
				}

			}
		}

		return participants;
	}

	/**
	 * If SSN matches completely or weight is greater than the cutoff adds 
	 * the participant to the matching participant list 
	 * @param participants
	 * @param weight
	 * @param existPpant
	 */
	protected void updateParticipantList(List participants, double weight, Participant existPpant)
	{
		if (isSSNOrPMI || weight >= cutoffPoints)
		{

			DefaultLookupResult result = new DefaultLookupResult();

			/**
			 *  Removed probability after discussion with Mark -- bug number 558
			*/
			result.setObject(existPpant);
			participants.add(result);
			result.setWeight(weight);
			result.setExactMatching(exactMatch);

		}

	}

	/**
	 * This function compares the two Social Security Numbers. 
	 * The criteria used for partial match is --> Mismatch of single digit with difference = 1 or any consecutive 
	 * pair of digits are transposed it is considered a partial match.  
	 * Only one occurrence of either of these is considered.
	 * 
	 * @param userSecurityNumber - Social Security Number of user
	 * @param existingSecurityNumber - Social Security Number of Participant from database
	 * @param - if this boolean variable is true then points for SSN will be taken else points of PMI
	 * @return int - points for complete, partial or no match
	 */

	protected int checkNumber(String userNumber, String existingNumber, boolean ssnOrPMI)
	{
		isSSNOrPMI = false;
		// complete match
		if (existingNumber.equals(userNumber))
		{
			isSSNOrPMI = true;
			if (ssnOrPMI)
			{
				return POINTSFORSSNEXACT;
			}
			else
			{
				return POINTSFORPMIEXACT;
			}
		}
		else
		// partial match
		{

			int count = 0; // to count total number of digits mismatched
			int temp = -1; // temporary variable used for storing value of other variable
			boolean areConDgtsTrped = false; // to check whether consecutive digits are transposed
			boolean isDifferenceOne = false; // to check whether difference of two digits is one

			if (userNumber.length() == existingNumber.length())
			{
				for (int i = 0; i < userNumber.length(); i++)
				{
					if (userNumber.charAt(i) != existingNumber.charAt(i))
					{
						if (temp == -1)
						{
							if (isDifferenceOne == false
									&& Math.abs(userNumber.charAt(i) - existingNumber.charAt(i)) == 1)
							{
								isDifferenceOne = true;
							}
							temp = i;

						}
						count++;
						if (count == 2 && i == temp + 1)
						{
							if (userNumber.charAt(i - 1) == existingNumber.charAt(i)
									&& userNumber.charAt(i) == existingNumber.charAt(i - 1))
							{
								areConDgtsTrped = true;
							}
							else
							{
								break;
							}
						}
					}
				}

			}

			/** 
			 * Mismatch of single digit with difference = 1 or any consecutive 
			 * pair of digits are transposed it is considered a partial match.  
			 * Only one occurrence of either of these is considered.
			 */
			if (count == 1 && isDifferenceOne == true || areConDgtsTrped == true && count == 2)
			{
				if (ssnOrPMI)
				{
					return POINTSFORSSNPARTIAL;
				}
				else
				{
					return POINTSFORPMIPARTIAL;
				}
			}

		}
		return 0;
	}

	/**
	 * This function compares the two Date Of Births.
	 * The criteria used for partial match is --> If the year and month are equal or day and year are equal
	 * or if the month and day are equal and the year is off by no more than 2 years.
	 * 
	 * @param userBirthDate - Birth Date of user
	 * @param existingBirthDate - Birth Date of Participant from database
	 * @return int - points for complete, partial or no match
	 */

	protected int checkDateOfBirth(Date userBirthDate, Date existingBirthDate)
	{
		int val = 0;
		// complete match
		if (userBirthDate.compareTo(existingBirthDate) == 0)
		{
			val = POINTSFORDOBEXACT;
		}
		// partial match
		else if ((userBirthDate.getMonth() == existingBirthDate.getMonth() && userBirthDate
				.getYear() == existingBirthDate.getYear())
				|| (userBirthDate.getDate() == existingBirthDate.getDate() && userBirthDate
						.getMonth() == existingBirthDate.getMonth())
				&& (Math.abs(userBirthDate.getYear() - existingBirthDate.getYear()) <= 2)
				|| (userBirthDate.getDate() == existingBirthDate.getDate() && userBirthDate
						.getYear() == existingBirthDate.getYear()))
		{
			val = POINTSFORDOBPARTIAL;
			exactMatch = false;
		}
		return val;
	}

	/**
	 * This function compares the two Last Names. 
	 * The criteria used for partial match is --> If the first 5 characters of the last name match then it
	 * is considered a partial match.  We also do a metaphone match on the last name.  Metaphone is a 
	 * standard algorithm that is applied to names to match those that sound alike.  If the metaphone 
	 * matches it is also considered partial.
	 * 
	 * @param userLastName - Last Name of user
	 * @param existingLastName - Last Name of Participant from database
	 * @return int - points for complete, partial or no match
	 */
	protected int checkLastName(String userLastName, String existingLastName)
	{
		int val = 0;
		// complete match
		if (userLastName.compareTo(existingLastName) == 0)
		{
			val = POINTSFORLASTNAMEEXACT;
		}
		// partial match --> Checks whether first 5 digits or meta phones of two last names are equal
		else if (userLastName.regionMatches(true, 0, existingLastName, 0,
				MATCHCHARACTERSFORLASTNAME))
		{
			val = POINTSFORLASTNAMEPARTIAL;
			exactMatch = false;
		}

		return val;
	}

	/**
	 * This function compares the two First Names. 
	 * The criteria used for partial match is --> If the first character matches,
	 * it is considered a partial match
	 * 
	 * @param userFirstName - First Name of user
	 * @param existingFirstName - First Name of Participant from database
	 * @return int - points for complete, partial or no match
	 */
	protected int checkFirstName(String userName, String existingName)
	{
		int val = 0;
		// complete match
		if (userName.compareTo(existingName) == 0)
		{
			val = POINTSFORFIRSTNAMEEXACT;
		}
		else if (userName.charAt(0) == existingName.charAt(0)) // partial match
		{
			val = POINTSFORFIRSTNAMEPARTIAL;
			exactMatch = false;
		}

		return val;
	}

	/**
	 * This function compares the two Middle Names. 
	 * The criteria used for partial match is --> If the first character matches, or if one has it 
	 * and other does not it is considered a partial match. 
	 * 
	 * @param userMiddleName - Middle Name of user
	 * @param existingMiddleName - Middle Name of Participant from database
	 * @return int - points for complete, partial or no match
	 */
	protected int checkMiddleName(String userName, String existingName)
	{
		boolean userNameBlank = false;
		boolean existingNameBlank = false;
		if (userName == null || userName.trim().equals(""))
		{
			userNameBlank = true;
		}
		if (existingName == null || existingName.trim().equals(""))
		{
			existingNameBlank = true;
		}

		/**
		 *  Removed this condition after discussion with Mark -- bug number 558
		 */

		if (userNameBlank == false && existingNameBlank == false)
		{
			// complete match
			if (userName.trim().compareTo(existingName.trim()) == 0)
			{
				return POINTSFORMIDDLENAMEEXACT;
			}
			else if (userName.trim().charAt(0) == existingName.trim().charAt(0)) // partial match
			{
				exactMatch = false;
				return POINTSFORMIDDLENAMEPARTIAL;

			}
		}

		return 0;
	}

	/**
	 * This function compares the two Races. 
	 * The criteria used for partial match is --> A partial is considered if one is missing and the other is there.
	 *  (eg, missing from the input data but in the database or viceversa).
	 * 
	 * @param userRace - Race of user
	 * @param existingRace - Race of Participant from database
	 * @return int - points for complete, partial or no match
	 */

	protected int checkRace(Collection userRace, Collection existingRace)
	{

		// complete match
		if (userRace != null && userRace.isEmpty() == false && existingRace != null
				&& existingRace.isEmpty() == false)
		{
			if (userRace.equals(existingRace))
			{
				return POINTSFORRACEEXACT;
			}
		}
		/**
		 *  Removed this condition after discussion with Mark -- bug number 558
		 */

		return 0;
	}

	/**
	 *
	 * This function compares the two ParticipantMedicalIdentifier. 
	 * The criteria used for partial match is --> A partial is considered if one is missing and the other is there.
	 *  (eg, missing from the input data but in the database or vice versa).
	 * 
	 * @param usrPartMedId - Race of user
	 * @param existingParticipantMedicalIdentifier - Race of Participant from database
	 * @return int - points for complete, partial or no match
	 */
	protected int checkParticipantMedicalIdentifier(Collection usrPartMedId,
			Participant existParticipant)
	{
		List pmiList = new ArrayList();
		Collection existPpantMediId = existParticipant.getParticipantMedicalIdentifierCollection();
		existParticipant.setParticipantMedicalIdentifierCollection(null);
		int ppantMediIdWght = 0;
		if (usrPartMedId != null && existPpantMediId != null)
		{
			Iterator estPntintMedIdItr = existPpantMediId.iterator();
			while (estPntintMedIdItr.hasNext())
			{
				String existmedRecordNo = (String) estPntintMedIdItr.next();
				String existingSiteId = (String) estPntintMedIdItr.next();
				Iterator usrPartMediIdItr = usrPartMedId.iterator();
				while (usrPartMediIdItr.hasNext())
				{
					ParticipantMedicalIdentifier participantId = (ParticipantMedicalIdentifier) usrPartMediIdItr
							.next();
					String siteId = participantId.getSite().getId().toString();
					String medicalRecordNo = participantId.getMedicalRecordNumber();
					if (existingSiteId != null && siteId.equals(existingSiteId))
					{
						ppantMediIdWght = ppantMediIdWght
								+ checkNumber(medicalRecordNo, existmedRecordNo, false);
						pmiList.add(existmedRecordNo);
						pmiList.add(existingSiteId);
						existParticipant.setParticipantMedicalIdentifierCollection(pmiList);
					}
				}
			}
			return ppantMediIdWght;
		}
		return 0;
	}

	/**
	 * This function compares the two Genders. 
	 * 
	 * @param userGender - Gender of user
	 * @param existingGender - Gender of Participant from database
	 * @return int - points for complete or no match
	 */
	protected int checkGender(String userGender, String existingGender)
	{
		int returnVal = 0;
		if (userGender.equals(existingGender))
		{
			returnVal = POINTSFORGENDEREXACT;
		}
		return returnVal;
	}

	/**
	 * This function checks whether first name and last name are flipped. 
	 * The criteria used for partial match is --> The first and last names are first parsed to determine 
	 * if multiple names exist in either field separated by a space, dash or comma.  If so they are split 
	 * and placed in the appropriate fields.  We also do a flip of the first and last names and try that 
	 * because sometimes people don’t enter them correctly (put last name in first name field and viceversa).
	 * 
	 * @param userFirstName - First Name of user
	 * @param userLastName - Last Name of user
	 * @param existPpantFName - First Name of Existing Participant
	 * @param existPpantLName - Last Name of existing Participant
	 * @return int - points for complete, partial or no match
	 */

	protected int checkFlipped(String userFirstName, String userLastName, String existPpantFName,
			String existPpantLName)
	{
		boolean usrFirstNameBlank = false; // tells whether first name of user is empty
		boolean userLastNameBlank = false; // tells whether first name of user is empty
		int pntfrFstnLstPrtl = POINTSFORFIRSTNAMEPARTIAL + POINTSFORLASTNAMEPARTIAL;

		if (userFirstName == null || userFirstName.trim().equals(""))
		{
			usrFirstNameBlank = true;
		}
		if (userLastName == null || userLastName.trim().equals(""))
		{
			userLastNameBlank = true;
		}

		if (existPpantFName != null)
		{
			existPpantFName = existPpantFName.trim();
		}
		if (existPpantLName != null)
		{
			existPpantLName = existPpantLName.trim();
		}

		if (usrFirstNameBlank == true && userLastNameBlank == false || usrFirstNameBlank == false
				&& userLastNameBlank == true)
		{
			String temp = userFirstName;
			// temp points to the String which is not blank
			if (usrFirstNameBlank == true)
			{
				temp = userLastName;
			}

			// Check if 2 names exist in either field separated by a space, dash or comma
			String split[] = temp.trim().split("[ -,]");

			/**
			 * If first name and last name entered by user in any of First Name/Last name matches with 
			 * First Name and Last name of existing participant or vice versa, points are given as sum of 
			 * points for partial match of first name and partial match of Last Name
			 */

			if (split != null && split.length == 2)
			{
				if ((split[0].trim().equalsIgnoreCase(existPpantLName) && split[1].trim()
						.equalsIgnoreCase(existPpantFName))
						|| (split[1].trim().equalsIgnoreCase(existPpantLName) && split[0].trim()
								.equalsIgnoreCase(existPpantFName)))
				{
					return pntfrFstnLstPrtl;
				}

			}
		}

		// check whether first name and last name are flipped
		if (usrFirstNameBlank == false && userFirstName.trim().equalsIgnoreCase(existPpantLName)
				&& userLastNameBlank == false
				&& userLastName.trim().equalsIgnoreCase(existPpantFName))
		{
			return pntfrFstnLstPrtl;
		}

		// check if user's first name matches with last name of existing participant
		if (usrFirstNameBlank == false && userFirstName.trim().equalsIgnoreCase(existPpantLName))
		{
			return POINTSFORFIRSTNAMEPARTIAL;
		}

		// check if user's last name matches with first name of existing participant
		if (userLastNameBlank == false && userLastName.trim().equalsIgnoreCase(existPpantFName))
		{
			return POINTSFORLASTNAMEPARTIAL;
		}
		return 0;
	}
}