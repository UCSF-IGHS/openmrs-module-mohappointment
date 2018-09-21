package org.openmrs.module.mohappointment.web.controller;

		import java.text.SimpleDateFormat;
		import java.util.ArrayList;
		import java.util.Date;
		import java.util.List;
		import javax.servlet.http.HttpServletRequest;
		import javax.servlet.http.HttpServletResponse;
		import org.apache.commons.logging.Log;
		import org.apache.commons.logging.LogFactory;
		import org.openmrs.api.context.Context;
		import org.openmrs.module.mohappointment.model.AppointmentView;
		import org.openmrs.module.mohappointment.service.IAppointmentService;
		import org.openmrs.module.mohappointment.utils.AppointmentUtil;
		import org.openmrs.module.mohappointment.utils.ContextProvider;
		import org.openmrs.module.mohappointment.utils.FileExporterUtil;
		import org.springframework.web.servlet.ModelAndView;
		import org.springframework.web.servlet.mvc.ParameterizableViewController;

		public class SearchAppointmentFormController
		extends ParameterizableViewController
		{
		private Log log = LogFactory.getLog(getClass());

		public SearchAppointmentFormController() {}

		protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(getViewName());

		IAppointmentService ias = (IAppointmentService)Context.getService(IAppointmentService.class);

		List<AppointmentView> appointments = request.getParameter("patient") == null ? null :
		getAppointments(request, mav);

		if (request.getParameter("export") != null) {
		FileExporterUtil util = new FileExporterUtil();
		util.exportAppointments(request, response, getAppointments(request,
		mav));
		}
		mav.addObject("appointments", appointments);

		mav.addObject("reasonForAppointmentOptions",
		AppointmentUtil.createConceptCodedOptions(Integer.valueOf(6189)));
		mav.addObject("appointmentStates", ias.getAppointmentStates());
		mav.addObject("areasToSee", ias.getServices());
		mav.addObject("today", Context.getDateFormat().format(new Date()));
		mav.addObject("creator", Context.getAuthenticatedUser());
		mav.addObject("reportName",
		ContextProvider.getMessage("mohappointment.export.report.search.result"));

		return mav;
		}

		private List<AppointmentView> getAppointments(HttpServletRequest request, ModelAndView mav)
		{
		IAppointmentService ias = (IAppointmentService)Context.getService(IAppointmentService.class);

		try
		{
		String patientId = (request.getParameter("patient") != null) &&
		(request.getParameter("patient").trim().compareTo("") != 0) ? request
		.getParameter("patient") :
		null;
		String providerId = (request.getParameter("provider") != null) &&
		(request.getParameter("provider").trim().compareTo("") != 0) ? request
		.getParameter("provider") :
		null;
		String locationId = (request.getParameter("location") != null) &&
		(request.getParameter("location").trim().compareTo("") != 0) ? request
		.getParameter("location") :
		null;
		Date dateFrom = (request.getParameter("dateFrom") != null) &&
		(request.getParameter("dateFrom").trim().compareTo("") != 0) ?
		Context.getDateFormat().parse(request.getParameter("dateFrom")) :
		null;
		Date dateTo = (request.getParameter("dateTo") != null) &&
		(request.getParameter("dateTo").trim().compareTo("") != 0) ?
		Context.getDateFormat().parse(request.getParameter("dateTo")) :
		null;
		String stateOfApp = (request.getParameter("stateofappointment") != null) &&
		(request.getParameter("stateofappointment").trim().compareTo("") != 0) ? request
		.getParameter("stateofappointment") :
		null;
		Integer reasonOfApp = (request.getParameter("reasonofappointment") != null) &&
		(request.getParameter("reasonofappointment").trim().compareTo("") != 0) ?
		Integer.valueOf(request.getParameter("reasonofappointment")) :
		null;

		mav.addObject("parameters", createAdditionalParameters(patientId,
		providerId, locationId, dateFrom, dateTo, stateOfApp,
		reasonOfApp));

		Object[] conditions = { patientId, providerId, locationId,
		dateFrom, 0, dateTo, stateOfApp, reasonOfApp };

		List<Integer> appointmentIds = ias.getAppointmentIdsByMulti(
		conditions, 100);

		List<AppointmentView> appointments = new ArrayList();
		for (Integer appointmentId : appointmentIds)
		{
		appointments.add(
		AppointmentUtil.convertIntoAppointmentViewObject(ias
		.getAppointmentById(appointmentId.intValue())));
		}

		return appointments;
		} catch (Exception e) {
		log.error("------------------------ " + e.getMessage() +
		" -------------------------");
		e.printStackTrace(); }
		return new ArrayList();
		}

		private String createAdditionalParameters(String patientId, String providerId, String locationId, Date dateFrom, Date dateTo, String stateOfApp, Integer reasonOfApp)
		{
		String parameters = "";

		parameters = parameters + (patientId != null ? "&patient=" + patientId : "");
		parameters = parameters + (providerId != null ? "&provider=" + providerId : "");
		parameters = parameters + (locationId != null ? "&location=" + locationId : "");
		parameters = parameters + (dateFrom != null ? "&dateFrom=" +
		Context.getDateFormat().format(dateFrom) : "");
		parameters = parameters + (dateTo != null ? "&dateTo=" +
		Context.getDateFormat().format(dateTo) : "");
		parameters = parameters + (stateOfApp != null ? "&stateofappointment=" +
		stateOfApp : "");
		parameters = parameters + (reasonOfApp != null ? "&reasonofappointment=" +
		reasonOfApp.intValue() : "");

		return parameters;
		}
		}