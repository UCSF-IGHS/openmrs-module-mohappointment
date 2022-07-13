/**
 *
 */
package org.openmrs.module.mohappointment.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.service.AppointmentService;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.openmrs.module.mohappointment.utils.FileExporterUtil;
import org.openmrs.web.WebConstants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * @author Yves GAKUBA
 *
 */
public class AppointmentServiceListController extends
		ParameterizableViewController {
	@RequestMapping(value = "module/mohappointment/service.list", method = RequestMethod.POST)
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(getViewName());

		if (request.getParameter("deleteService") != null) {

			boolean deleted = deleteService(request, mav);

			if (deleted)
				request.getSession().setAttribute(
						WebConstants.OPENMRS_MSG_ATTR, "Service Removed");
			else
				request.getSession()
						.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
								"Service NOT DELETED!");
		}

		if (request.getParameter("export") != null) {
			FileExporterUtil xprt = new FileExporterUtil();
			xprt.exportToCSVFile(request, response, "List of current services");
		}

		AppointmentService ias = Context.getService(AppointmentService.class);
		mav.addObject("services", ias.getServices());
		mav.addObject("today", Context.getDateFormat().format(new Date()));
		mav.addObject("creator", Context.getAuthenticatedUser());
		mav.addObject("reportName",
				"mohappointment.appointment.service.current");

		return mav;
	}

	/**
	 * Deletes/Voids the Service that was clicked
	 *
	 * @param request
	 *            the HttpServletRequest
	 * @param mav
	 *            ModelAndView that allows us to display/send to page/view
	 * @return true when deleted successfully, false otherwise
	 * @throws Exception
	 */
	private boolean deleteService(HttpServletRequest request, ModelAndView mav)
			throws Exception {

		AppointmentService ias = Context.getService(AppointmentService.class);

		if (request.getParameter("deleteService") != null)
			if (request.getParameter("deleteService").equals("true")) {

				Services service = ias.getServiceById(Integer.valueOf(request
						.getParameter("serviceId")));

				mav.addObject("servId", service.getServiceId());

				service.setRetired(true);
				service.setRetireDate(new Date());
				service.setRetiredBy(Context.getAuthenticatedUser());
				service.setRetireReason("The service is no longer in use");

				ias.saveService(service);

				/** Remove/Void all corresponding ServiceProviders */
				AppointmentUtil.removeServiceProvidersHavingThisService(service);

			}
		return true;
	}
}
