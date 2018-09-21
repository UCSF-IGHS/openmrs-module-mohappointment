/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.openmrs.Patient
 *  org.openmrs.api.context.Context
 *  org.openmrs.module.mohappointment.model.Appointment
 *  org.openmrs.module.mohappointment.service.IAppointmentService
 *  org.openmrs.module.mohappointment.utils.AppointmentUtil
 *  org.openmrs.web.controller.PortletController
 */
package org.openmrs.module.mohappointment.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.service.IAppointmentService;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.openmrs.web.controller.PortletController;

public class AppointmentDashboardPortletController
		extends PortletController {
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		IAppointmentService ias = (IAppointmentService)Context.getService(IAppointmentService.class);
		try {
			AppointmentUtil.setAttendedAppointment((HttpServletRequest)request);
			AppointmentUtil.cancelAppointment((HttpServletRequest)request);
			Patient p = Context.getPatientService().getPatient(Integer.valueOf(request.getParameter("patientId")));
			Object[] arrobject = new Object[8];
			arrobject[0] = p.getPatientId();
			Object[] conditions = arrobject;
			List<Integer> appointmentIds = ias.getAppointmentIdsByMulti(conditions, 100);
			ArrayList<Appointment> appointments = new ArrayList<Appointment>();
			for (Integer appointmentId : appointmentIds) {
				appointments.add(ias.getAppointmentById(appointmentId.intValue()));
			}
			request.setAttribute("appointments", appointments);
			request.setAttribute("patientId", (Object)p.getPatientId());
		}
		catch (Exception e) {
			this.log.error((Object)">>>>>>>>>>> APPOINTMENT >> An error occured when trying to load appointments on the patient dashboard");
			e.printStackTrace();
		}
		super.populateModel(request, model);
	}
}