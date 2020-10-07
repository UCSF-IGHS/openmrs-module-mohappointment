package org.openmrs.module.mohappointment.web.dwr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.service.AppointmentService;
import org.openmrs.web.dwr.PersonListItem;


public class DWRAppointmentUtil
{
	public DWRAppointmentUtil() {}

	public String getPatientListInTable(String searchString, String id)
	{
		PersonListItem ret = null;
		List<Integer> appointments = null;
		List<Patient> matchingPatients = findPatientsByIdentifier(searchString);

		AppointmentService ias = (AppointmentService)Context.getService(AppointmentService.class);
		Object[] conditions = { matchingPatients.size() > 0 ? Integer.valueOf(((Patient)matchingPatients.get(0)).getPatientId().intValue()) : null, null, null, null, Boolean.valueOf(false), null, null, null };

		appointments = ias.getAppointmentIdsByMulti(conditions, 50);


		StringBuilder sb = new StringBuilder("");
		sb.append("<table class='openmrsSearchTable' cellpadding=2 cellspacing=0 style='width:100%; font-size:0.8em'>");

		sb.append("<tr><td colspan='12' style='text-align:right; font-style:italic;'>Results for &quot;" + id + "&quot;: " + appointments.size() + " appointments</tr>");

		sb.append("<tr class='oddRow'><th>#</th><th>Identifier</th><th>Patient Names</th><th>Age</th><th>Gender</th><th></th><th>Birthdate</th><th>Appointment Date</th><th>Provider</th><th>Reason of Appointment</th><th>State</th><th></th><th></th></tr>");

		int i = 0;
		for (Integer appId : appointments) {
			Appointment app = ias.getAppointmentById(appId.intValue());
			Person ps = app.getPatient();
			i++;
			ret = new PersonListItem();
			ret.setPersonId(ps.getPersonId());
			ret.setGivenName(ps.getGivenName());
			ret.setMiddleName(ps.getMiddleName());
			ret.setFamilyName(ps.getFamilyName());
			ret.setGender(ps.getGender());
			ret.setBirthdate(ps.getBirthdate());
			ret.setBirthdateEstimated(ps.getBirthdateEstimated());
			String identifier = "";
			identifier = ps.isPatient() ? ((Patient)ps).getPatientIdentifier().getIdentifier() : "";


			String name = (ret.getGivenName() != null ? ret.getGivenName().trim() : "") + "&nbsp;" + (ret.getMiddleName() != null ? ret.getMiddleName().trim() : "") + "&nbsp;" + (ret.getFamilyName() != null ? ret.getFamilyName().trim() : "");


			String provName = app.getProvider().getPersonName().toString();
			String appDate = new SimpleDateFormat("dd-MMM-yyyy").format(app.getAppointmentDate());

			String reason = "";
			if ((app.getReason() != null) && (!app.getReason().getValueAsString(Context.getLocale()).equals("")))
			{

				reason = app.getReason().getValueAsString(Context.getLocale());
			}




			sb.append("<tr class='searchRow " + (i % 2 == 0 ? "oddRow" : "") + "'>");

			sb.append("<td class='searchIndex'>" + i + ".</td>");
			sb.append("<td class='patientIdentifier'>" + identifier + "</td>");
			sb.append("<td>" + name + "</td>");
			sb.append("<td style='text-align:center'>" + ps.getAge() + "</td>");
			if (ret.getGender().trim().compareToIgnoreCase("f") == 0) {
				sb.append("<td style='text-align:center'><img src='../../images/female.gif'/></td>");
			}
			else {
				sb.append("<td style='text-align:center'><img src='../../images/male.gif'/></td>");
			}
			sb.append("<td>" + (ret.getBirthdateEstimated().booleanValue() ? "&asymp;" : "") + "</td>");

			sb.append("<td>" + new SimpleDateFormat("dd-MMM-yyyy").format(ret.getBirthdate()) + "</td>");


			sb.append("<td>" + appDate + "</td>");
			sb.append("<td>" + app.getProvider().getPersonName() + "</td>");
			sb.append("<td>" + reason + "</td>");
			sb.append("<td>" + app.getAppointmentState().getDescription() + "</td>");

			sb.append("<td onclick=showDialog('" + app.getAppointmentId() + "','" + name.replace(" ", "&nbsp;") + "','" + provName.replace(" ", "&nbsp;") + "','" + appDate + "','" + reason.replace(" ", "&nbsp;") + "',1)><input type='button' value='WAITING/INADVANCE'/></td>");

			sb.append("<td onclick=showDialog('" + app.getAppointmentId() + "','" + name.replace(" ", "&nbsp;") + "','" + provName.replace(" ", "&nbsp;") + "','" + appDate + "','" + reason.replace(" ", "&nbsp;") + "',2)><input type='button' value='POSTPONE'/></td>");

			sb.append("</tr>");
		}
		sb.append("</table>");

		return sb.toString();
	}
	private List<Patient> findPatientsByIdentifier(String search)
	{
		PatientIdentifierType preferredIdentifierType = getPrimaryPatientIdentiferType();
		List<PatientIdentifier> ids = Context.getPatientService().getPatientIdentifiers(search, getPatientIdentifierTypesToUse(), null, null, null);


		List<Patient> ret = new ArrayList();


		for (PatientIdentifier id : ids) {
			if ((id.getIdentifierType().equals(preferredIdentifierType)) &&
					(!id.getPatient().isVoided().booleanValue())) {
				ret.add(id.getPatient());
			}
		}

		for (PatientIdentifier id : ids) {
			if ((!id.getIdentifierType().equals(preferredIdentifierType)) &&
					(!id.getPatient().isVoided().booleanValue())) {
				ret.add(id.getPatient());
			}
		}

		return ret;
	}

	private PatientIdentifierType getPrimaryPatientIdentiferType() {
		PatientIdentifierType pit = null;
		try {
			pit = Context.getPatientService().getPatientIdentifierType(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("registration.primaryIdentifierType")));



		}
		catch (Exception ex)
		{


			pit = Context.getPatientService().getPatientIdentifierTypeByName(Context.getAdministrationService().getGlobalProperty("registration.primaryIdentifierType"));
		}






		if (pit == null) {
			throw new RuntimeException("Cannot find patient identifier type specified by global property registration.primaryIdentifierType");
		}


		return pit;
	}

	private List<PatientIdentifierType> getPatientIdentifierTypesToUse()
	{
		List<PatientIdentifierType> ret = new ArrayList();
		ret.add(getPrimaryPatientIdentiferType());

		String s = Context.getAdministrationService().getGlobalProperty("registration.otherIdentifierTypes");

		if (s != null) {
			String[] ids = s.split(",");
			for (String idAsString : ids) {
				try {
					idAsString = idAsString.trim();
					if (idAsString.length() != 0)
					{
						PatientIdentifierType idType = null;
						try {
							Integer id = Integer.valueOf(idAsString);
							idType = Context.getPatientService().getPatientIdentifierType(id);
						}
						catch (Exception ex) {
							idType = Context.getPatientService().getPatientIdentifierTypeByName(idAsString);
						}

						if (idType == null) {
							throw new IllegalArgumentException("Cannot find patient identifier type " + idAsString + " specified in global property " + "registration.otherIdentifierTypes");
						}




						if (!ret.contains(idType))
							ret.add(idType);
					}
				} catch (Exception ex) {
					throw new IllegalArgumentException("Error in global property registration.otherIdentifierTypes near '" + idAsString + "'");
				}
			}
		}


		return ret;
	}
}