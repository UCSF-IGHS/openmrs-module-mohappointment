

package org.openmrs.module.mohappointment.web.controller;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.ServiceProviders;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.service.IAppointmentService;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

public class AppointmentServiceFormController
		extends ParameterizableViewController
{
	public AppointmentServiceFormController() {}

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		ModelAndView mav = new ModelAndView();
		mav.addObject(
				"medicalServices",

				AppointmentUtil.createConceptCodedOptions(Integer.valueOf(6702)));
		mav.setViewName(getViewName());

		if (request.getParameter("editService") != null)
		{
			editService(request, mav);
		}

		if (request.getParameter("save") != null) {
			boolean saved = saveService(request);
			if (saved) {
				request.getSession().setAttribute(
						"openmrs_msg", "Service Saved");
			} else {
				request.getSession().setAttribute(
						"openmrs_error", "Service Not Saved");
			}
			return new ModelAndView(new RedirectView("service.list"));
		}

		return mav;
	}

	private boolean saveService(HttpServletRequest request)
			throws Exception
	{
		IAppointmentService ias = (IAppointmentService)Context.getService(IAppointmentService.class);

		Services service = null;
		if ((request.getParameter("servId") != null) &&
				(!request.getParameter("servId").equals("")))
		{
			service = ias.getServiceById(Integer.valueOf(request
					.getParameter("servId")));
		}

		String serviceName = request.getParameter("name");
		String serviceDescription = request.getParameter("description");
		String concept = request.getParameter("serviceRelatedConcept");

		if (serviceName.trim().compareTo("") == 0) {
			return false;
		}
		if ((serviceName.equals("")) || (concept.equals(""))) {
			return false;
		}
		Services serv = null;
		if (service == null) {
			serv = new Services();
			serv.setName(serviceName);
			serv.setDescription(serviceDescription);
			if (Context.getConceptService().getConcept(
					Integer.valueOf(concept)) != null)
				serv.setConcept(Context.getConceptService().getConcept(
						Integer.valueOf(concept)));
			if (serv.getCreatedDate() == null)
				serv.setCreatedDate(new Date());
			if (serv.getCreator() == null)
				serv.setCreator(Context.getAuthenticatedUser());
			serv.setRetired(Boolean.valueOf(false));
		} else {
			serv = service;
			serv.setName(serviceName);
			serv.setDescription(serviceDescription);
			serv.setConcept(Context.getConceptService().getConcept(
					Integer.valueOf(concept)));
		}

		ias.saveService(serv);

		return true;
	}

	private void editService(HttpServletRequest request, ModelAndView mav)
			throws Exception
	{
		IAppointmentService ias = (IAppointmentService)Context.getService(IAppointmentService.class);

		if (request.getParameter("editService") != null)
		{
			Services service = ias.getServiceById(Integer.valueOf(request
					.getParameter("editServiceId")));

			mav.addObject("service", service);
			mav.addObject("servId", service.getServiceId());
		}
	}
	private boolean providerIsAlreadyAssignedThisService(Person provider, Services service)
	{
		IAppointmentService ias = (IAppointmentService)Context.getService(IAppointmentService.class);

		for (ServiceProviders sp : ias.getServiceProviders()) {
			if ((sp.getProvider().equals(provider)) &&
					(sp.getService().equals(service)) && (!sp.isVoided())) {
				return true;
			}
		}
		return false;
	}
}