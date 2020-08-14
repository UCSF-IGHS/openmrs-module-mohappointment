package org.openmrs.module.mohappointment.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;


public class AdminList
		extends AdministrationSectionExt
{
	public AdminList() {}

	public Extension.MEDIA_TYPE getMediaType()
	{
		return Extension.MEDIA_TYPE.html;
	}



	public String getTitle()
	{
		return "mohappointment.title";
	}




	public Map<String, String> getLinks()
	{
		Map<String, String> map = new HashMap();



		if (Context.getAuthenticatedUser().hasPrivilege("Search Appointments")) {
			map.put("module/mohappointment/advancedSearch.form", "mohappointment.search.title");
		}


		if (Context.getAuthenticatedUser().hasPrivilege("Manage Services and Providers")) {
			map.put("module/mohappointment/serviceProvider.list", "mohappointment.appointment.service.provider.manage");
		}
		if (Context.getAuthenticatedUser().hasPrivilege("Search Appointments")) {
			map.put("module/mohappointment/findAppointment.form", "mohappointment.appointment.find");
		}
		if (Context.getAuthenticatedUser().hasPrivilege("Create Appointment")) {
			map.put("module/mohappointment/addAppointment.form", "mohappointment.appointment.create");
		}
		return map;
	}

	public String getRequiredPrivilege()
	{
		return "View Appointments";
	}
}