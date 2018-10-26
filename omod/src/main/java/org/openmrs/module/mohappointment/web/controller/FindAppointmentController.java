package org.openmrs.module.mohappointment.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.AppointmentState;
import org.openmrs.module.mohappointment.service.IAppointmentService;
import org.openmrs.module.mohappointment.statepattern.State;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;







public class FindAppointmentController
		extends ParameterizableViewController
{
	private Log log = LogFactory.getLog(getClass());

	public FindAppointmentController() {}

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();

		if (request.getParameter("savechanges") != null) {
			log.info(">>>>>>>>>>>>>>>>>>><<<<<<<<<<<<>>>>>>>>> is changing ");
			saveAppointmentChanges(request);
		} else {
			log.info(">>>>>>>>>>>>>>>>>>><<<<<<<<<<<<>>>>>>>>> Cant be changed");
		}

		mav.setViewName(getViewName());

		return mav;
	}

	private void saveAppointmentChanges(HttpServletRequest request) {
		IAppointmentService ias = (IAppointmentService)Context.getService(IAppointmentService.class);

		Appointment changedAppointment = ias.getAppointmentById(Integer.valueOf(request.getParameter("appointmentId")).intValue());

		log.info(">>>>>>>>>>>>>>>>>>><<<<<<<<<<<<>>>>>>>>> " + changedAppointment);
		try
		{
			if (request.getParameter("appointmentState").compareTo("4") == 0)
			{



				log.info("---------1------->> " + changedAppointment.getState().toString());


				changedAppointment.getState().upcoming();
				changedAppointment.setAppointmentState(new AppointmentState(Integer.valueOf(3), "UPCOMING"));

				log.info("---------2------->> " + changedAppointment.getState().toString());
			}
			else if (request.getParameter("appointmentState").compareTo("5") == 0)
			{
				changedAppointment.getState().inAdvance();
			} else if (request.getParameter("appointmentState").compareTo("8") == 0)
			{
				changedAppointment.getState().postponed();
			}


			ias.saveAppointment(changedAppointment);
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Updated");
		} catch (Exception e) {
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> An error occured when trying to change the state...\n" + e.getMessage());


			e.printStackTrace();
		}
	}
}