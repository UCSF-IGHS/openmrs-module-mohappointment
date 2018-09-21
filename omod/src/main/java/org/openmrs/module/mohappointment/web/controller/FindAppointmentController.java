package org.openmrs.module.mohappointment.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.AppointmentState;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.service.IAppointmentService;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class FindAppointmentController
		extends ParameterizableViewController {
	private Log log = LogFactory.getLog(this.getClass());

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.addObject("services", (Object)AppointmentUtil.getAllServices());
		if (request.getParameter("savechanges") != null) {
			this.log.info((Object)">>>>>>>>>>>>>>>>>>><<<<<<<<<<<<>>>>>>>>> is changing ");
			this.saveAppointmentChanges(request);
		} else {
			this.log.info((Object)">>>>>>>>>>>>>>>>>>><<<<<<<<<<<<>>>>>>>>> Cant be changed");
		}
		mav.setViewName(this.getViewName());
		return mav;
	}

	private void saveAppointmentChanges(HttpServletRequest request) {
		IAppointmentService ias = (IAppointmentService)Context.getService(IAppointmentService.class);
		Appointment changedAppointment = ias.getAppointmentById(Integer.valueOf(request.getParameter("appointmentId")).intValue());
		this.log.info((Object)(">>>>>>>>>>>>>>>>>>><<<<<<<<<<<<>>>>>>>>> " + (Object)changedAppointment));
		try {
			if (request.getParameter("service") != null && !request.getParameter("service").equals("0")) {
				this.updateServiceToSee(request, changedAppointment);
			}
			if (request.getParameter("postponedDate") != null) {
				this.postponeAppointment(request, changedAppointment);
			}
			if (request.getParameter("appointmentState") != null) {
				this.updateAppointmentState(request, changedAppointment);
			}
			ias.saveAppointment(changedAppointment);
		}
		catch (Exception e) {
			this.log.info((Object)(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> An error occured when trying to change the state...\n" + e.getMessage()));
			e.printStackTrace();
		}
	}

	private void postponeAppointment(HttpServletRequest request, Appointment changedAppointment) throws ParseException {
		SimpleDateFormat df = OpenmrsUtil.getDateFormat((Locale)Context.getLocale());
		changedAppointment.setAppointmentState(new AppointmentState(Integer.valueOf(8), "POSTPONED"));
		changedAppointment.setAppointmentDate(df.parse(request.getParameter("postponedDate")));
	}

	private void updateAppointmentState(HttpServletRequest request, Appointment changedAppointment) {
		if (request.getParameter("appointmentState").compareTo("4") == 0) {
			changedAppointment.setAppointmentState(new AppointmentState(Integer.valueOf(4), "WAITING"));
		} else if (request.getParameter("appointmentState").compareTo("5") == 0) {
			changedAppointment.setAppointmentState(new AppointmentState(Integer.valueOf(5), "INADVANCE"));
		} else if (request.getParameter("appointmentState").compareTo("8") == 0) {
			changedAppointment.setAppointmentState(new AppointmentState(Integer.valueOf(8), "POSTPONED"));
		}
	}

	private void updateServiceToSee(HttpServletRequest request, Appointment changedAppointment) throws APIException, NumberFormatException {
		Concept concept = Context.getConceptService().getConcept(Integer.valueOf(Integer.parseInt(request.getParameter("service"))));
		changedAppointment.setService(AppointmentUtil.getServiceByConcept((Concept)concept));
		changedAppointment.getReason().setValueCoded(concept);
		Context.getObsService().saveObs(changedAppointment.getReason(), "The service was wrong! I just updated it to " + AppointmentUtil.getServiceByConcept((Concept)concept).getName());
	}
}