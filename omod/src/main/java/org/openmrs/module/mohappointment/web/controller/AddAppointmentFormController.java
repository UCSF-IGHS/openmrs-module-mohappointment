package org.openmrs.module.mohappointment.web.controller;

        import java.io.PrintStream;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.List;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import javax.servlet.http.HttpSession;
        import org.apache.commons.logging.Log;
        import org.apache.commons.logging.LogFactory;
        import org.openmrs.Patient;
        import org.openmrs.api.APIException;
        import org.openmrs.api.PatientService;
        import org.openmrs.api.PersonService;
        import org.openmrs.api.context.Context;
        import org.openmrs.module.mohappointment.model.Appointment;
        import org.openmrs.module.mohappointment.model.Services;
        import org.openmrs.module.mohappointment.utils.AppointmentUtil;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.servlet.ModelAndView;
        import org.springframework.web.servlet.mvc.ParameterizableViewController;

        public class AddAppointmentFormController
        extends ParameterizableViewController
        {
        private Log log = LogFactory.getLog(getClass());

        public AddAppointmentFormController() {}
        @RequestMapping(value = "module/mohappointment/addAppointment.form", method = RequestMethod.POST)
        protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
        {
        ModelAndView mav = new ModelAndView();
        mav.setViewName(getViewName());

        if ((request.getParameter("saveAppointment") != null) &&
        (request.getParameter("saveAppointment").equals("Save"))) {
        handleCreateNewAppointment(request, mav);
        }
        if ((request.getParameter("clinicalareatosee") != null) &&
        (!request.getParameter("clinicalareatosee").equals("")) &&
        (request.getParameter("patient") != null) &&
        (!request.getParameter("patient").equals(""))) {
        getAppointmentsByPatient(request, mav);
        }
        mav.addObject("reasonForAppointmentOptions",
        AppointmentUtil.createConceptCodedOptions(Integer.valueOf(6189)));
        mav.addObject("areasToSee", AppointmentUtil.getAllServices());

        return mav;
        }

        private void handleCreateNewAppointment(HttpServletRequest request, ModelAndView mav)
        throws APIException, NumberFormatException, ParseException
        {
        if ((request.getParameter("patient") != null) &&
        (request.getParameter("clinicalareatosee") != null) &&
        (request.getParameter("appointmentDate") != null) &&
        (request.getParameter("saveAppointment") != null) &&
        (!request.getParameter("patient").equals("")) &&
        (!request.getParameter("clinicalareatosee").equals("")) &&
        (!request.getParameter("appointmentDate").equals("")) &&
        (request.getParameter("saveAppointment").equals("Save")))
        {
        Appointment appointment = new Appointment();

        appointment.setPatient(Context.getPatientService().getPatient(
        Integer.valueOf(Integer.parseInt(request.getParameter("patient")))));
        appointment.setAppointmentDate(Context.getDateFormat().parse(
        request.getParameter("appointmentDate")));
        appointment.setLocation(AppointmentUtil.getDefaultLocation());
        appointment.setService(AppointmentUtil.getServiceById(
        Integer.valueOf(Integer.parseInt(request.getParameter("clinicalareatosee")))));
        appointment.setNextVisitDate(null);
        appointment.setReason(null);

        if (request.getParameter("note") != null) {
        appointment.setNote(request.getParameter("note"));

        mav.addObject("note", request.getParameter("note"));
        }

        mav.addObject("appointmentDate",
        request.getParameter("appointmentDate"));

        appointment.setCreatedDate(new Date());
        appointment.setCreator(Context.getAuthenticatedUser());
        appointment.setAttended(Boolean.valueOf(false));
        appointment.setVoided(false);

        if ((request.getParameter("providerId") != null) &&
        (!request.getParameter("providerId").equals("")))
        {
        appointment.setProvider(Context.getPersonService().getPerson(
        Integer.valueOf(Integer.parseInt(request.getParameter("providerId")))));
        }


        if (!AppointmentUtil.alreadyHasAppointmentThere(appointment.getPatient(), appointment.getAppointmentDate(), appointment.getService()).booleanValue())
        {
        if (appointment.getAppointmentDate().compareTo(new Date()) > 0)
        {
        AppointmentUtil.saveUpcomingAppointment(appointment);
        request.getSession().setAttribute(
        "openmrs_msg",
        "The appointment for - " +
        appointment.getPatient().getFamilyName() +
        " " + appointment.getPatient().getGivenName() +
        " - is created successfully !");
        } else {
        request.getSession().setAttribute(
        "openmrs_error",
        "Appointment date must be in the future! - " +
        Context.getDateFormat().format(appointment
        .getAppointmentDate()) +
        " - belongs to the past...");
        }
        mav.addObject("displayAppointments", "displayAppointments");
        } else {
        request.getSession().setAttribute(
        "openmrs_error",
        " - " + appointment.getPatient().getFamilyName() + " " +
        appointment.getPatient().getGivenName() +
        " - already has an appointment in - " +
        appointment.getService().getName() +
        " - on this date: - " +
        Context.getDateFormat().format(appointment
        .getAppointmentDate()) + " - !");
        }

        System.out.println("\n___________________ SAVED APPOINTMENT ________________\n" +
        appointment.toString());
        }
        }

        private ModelAndView getAppointmentsByPatient(HttpServletRequest request, ModelAndView mav)
        throws NumberFormatException
        {
        if ((request.getParameter("clinicalareatosee") != null) &&
        (!request.getParameter("clinicalareatosee").equals("")) &&
        (request.getParameter("patient") != null) &&
        (!request.getParameter("patient").equals("")))
        {
        Services service = AppointmentUtil.getServiceById(
        Integer.valueOf(Integer.parseInt(request.getParameter("clinicalareatosee"))));

        List<Appointment> appointments =
        AppointmentUtil.getAppointmentsByPatientAndDate(
        Context.getPatientService().getPatient(
        Integer.valueOf(Integer.parseInt(request
        .getParameter("patient")))),
        service, null);

        mav.addObject("appointments", appointments);
        mav.addObject("displayAppointments", "displayAppointments");
        mav.addObject("clinicalareatosee",
        request.getParameter("clinicalareatosee"));
        mav.addObject("patient", request.getParameter("patient"));

        mav.addObject("reasonForAppointmentOptions",
        AppointmentUtil.createConceptCodedOptions(Integer.valueOf(6189)));
        mav.addObject("areasToSee", AppointmentUtil.getAllServices());

        return mav;
        }
        return null;
        }
        }