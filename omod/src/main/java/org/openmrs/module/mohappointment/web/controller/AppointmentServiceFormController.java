/**
 * 
 */
package org.openmrs.module.mohappointment.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.ServiceProviders;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.service.IAppointmentService;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.openmrs.module.mohappointment.utils.ConstantValues;
import org.openmrs.web.WebConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Yves GAKUBA
 * 
 */
public class AppointmentServiceFormController extends
		ParameterizableViewController {
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.addObject(
				"medicalServices",
				AppointmentUtil
						.createConceptCodedOptions(ConstantValues.PRIMARY_CARE_SERVICE_REQUESTED));
		mav.setViewName(getViewName());

		if (request.getParameter("editService") != null) {

			editService(request, mav);
		}

		if (request.getParameter("save") != null) {
			boolean saved = saveService(request);
			if (saved)
				request.getSession().setAttribute(
						WebConstants.OPENMRS_MSG_ATTR, "Service Saved");
			else
				request.getSession().setAttribute(
						WebConstants.OPENMRS_ERROR_ATTR, "Service Not Saved");
			
			return new ModelAndView(new RedirectView("service.list"));
		}

		return mav;
	}

	/**
	 * Auto generated method comment
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private boolean saveService(HttpServletRequest request) throws Exception {
		IAppointmentService ias = Context.getService(IAppointmentService.class);

		Services service = null;
		if (request.getParameter("servId") != null) {
			if (!request.getParameter("servId").equals(""))

				System.out.println("_____APPOINTMENT SERVICE_______ID : "+request.getParameter("servId"));
				service = ias.getServiceById(Integer.valueOf(request
						.getParameter("servId")));
		}

		String serviceName = request.getParameter("name");
		String serviceDescription = request.getParameter("description");
		String concept = request.getParameter("serviceRelatedConcept");

		if (serviceName.trim().compareTo("") == 0) {
			return false;
		}
		if (serviceName.equals("") || concept.equals("")) {
			return false;
		} else {
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
				serv.setRetired(false);
			}else{
				serv = service;
				serv.setName(serviceName);
				serv.setDescription(serviceDescription);
				serv.setConcept(Context.getConceptService().getConcept(
							Integer.valueOf(concept)));
			}
			
			ias.saveService(serv);
		}
		return true;
	}

	/**
	 * Edits/Updates Service with the new Values selected by the User
	 * 
	 * @param request
	 *            the HTTPServletRequest object
	 * @param mav
	 *            the ModelAndView object that allows us to display/sent to
	 *            page/view
	 * @throws Exception
	 */
	private void editService(HttpServletRequest request, ModelAndView mav)
			throws Exception {

		IAppointmentService ias = Context.getService(IAppointmentService.class);

		if (request.getParameter("editService") != null) {

			Services service = ias.getServiceById(Integer.valueOf(request
					.getParameter("editServiceId")));

			mav.addObject("service", service);
			mav.addObject("servId", service.getServiceId());

		}
	}

	/**
	 * Checks whether the entered Provider and Service are not already
	 * associated in the ServiceProviders list
	 * 
	 * @param provider
	 *            the provider to be matched
	 * @param service
	 *            the service to be matched
	 * @return true if they are already associated
	 */
	private boolean providerIsAlreadyAssignedThisService(Person provider,
			Services service) {
		IAppointmentService ias = Context.getService(IAppointmentService.class);

		for (ServiceProviders sp : ias.getServiceProviders()) {
			if (sp.getProvider().equals(provider)
					&& sp.getService().equals(service) && !sp.isVoided())
				return true;
		}

		return false;
	}

}
