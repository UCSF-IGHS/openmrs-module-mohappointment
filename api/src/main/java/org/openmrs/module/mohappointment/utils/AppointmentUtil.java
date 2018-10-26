/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.openmrs.Concept
 *  org.openmrs.ConceptAnswer
 *  org.openmrs.Encounter
 *  org.openmrs.GlobalProperty
 *  org.openmrs.Location
 *  org.openmrs.Obs
 *  org.openmrs.Patient
 *  org.openmrs.Person
 *  org.openmrs.User
 *  org.openmrs.api.context.Context
 *  org.openmrs.module.mohappointment.model.Appointment
 *  org.openmrs.module.mohappointment.model.AppointmentState
 *  org.openmrs.module.mohappointment.model.AppointmentView
 *  org.openmrs.module.mohappointment.model.ServiceProviders
 *  org.openmrs.module.mohappointment.model.Services
 *  org.openmrs.module.mohappointment.service.IAppointmentService
 */
package org.openmrs.module.mohappointment.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.AppointmentState;
import org.openmrs.module.mohappointment.model.AppointmentView;
import org.openmrs.module.mohappointment.model.ServiceProviders;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.service.AppointmentService;

public class AppointmentUtil {
    private static Log log = LogFactory.getLog(AppointmentUtil.class);

    public static AppointmentService getAppointmentService() {
        return (AppointmentService)Context.getService(AppointmentService.class);
    }

    public static HashMap<Integer, String> createConceptCodedOptions(Integer codedConceptQuestionId) {
        HashMap<Integer, String> answersMap = new HashMap<Integer, String>();
        Concept questionConcept = Context.getConceptService().getConcept(Integer.valueOf(codedConceptQuestionId));
        if (questionConcept != null) {
            for (ConceptAnswer ca : questionConcept.getAnswers()) {
                answersMap.put(ca.getAnswerConcept().getConceptId(), ca.getAnswerConcept().getDisplayString());
            }
        }
        return answersMap;
    }

    public static AppointmentView convertIntoAppointmentViewObject(Appointment app) {
        Services services = null;
        services = app.getReason() != null ? AppointmentUtil.getServiceByConcept(app.getReason().getValueCoded()) : app.getService();
        AppointmentView view = new AppointmentView();
        view.setAppointmentId(app.getAppointmentId());
        view.setAppointmentDate(app.getAppointmentDate());
        view.setAppointmentState(app.getAppointmentState());
        view.setEncounter(app.getEncounter());
        view.setAttended(app.getAttended());
        view.setLocation(app.getLocation());
        view.setNextVisitDate(app.getNextVisitDate());
        view.setPatient(app.getPatient());
        view.setProvider(app.getProvider());
        view.setReason(app.getReason());
        view.setVoided(app.isVoided());
        view.setService(services);
        view.setPatientUrl(AppointmentUtil.getPatientURL(services, null));
        return view;
    }

    public static List<AppointmentView> convertIntoAppointmentViewList(List<Appointment> appointments) {
        ArrayList<AppointmentView> views = new ArrayList<AppointmentView>();
        for (Appointment app : appointments) {
            views.add(AppointmentUtil.convertIntoAppointmentViewObject(app));
        }
        return views;
    }

    public static Services getServiceByConcept(Concept concept) {
        AppointmentService ias = AppointmentUtil.getAppointmentService();
        if (ias != null) {
            for (Services service : ias.getServices()) {
                if (service.getConcept().getConceptId().intValue() != concept.getConceptId().intValue()) continue;
                return service;
            }
        }
        return null;
    }

    public static boolean cancelAppointment(HttpServletRequest request) {
        Integer appointmentId = 0;
        AppointmentService service = AppointmentUtil.getAppointmentService();
        if (request.getParameter("appointmentId") != null && !request.getParameter("appointmentId").equalsIgnoreCase("")) {
            appointmentId = Integer.valueOf(request.getParameter("appointmentId"));
            Appointment appointment = service.getAppointmentById(appointmentId.intValue());
            if (request.getParameter("cancel") != null && request.getParameter("cancel").equals("true")) {
                appointment.setVoided(true);
                appointment.setAppointmentState(new AppointmentState(Integer.valueOf(1), "NULL"));
                service.saveAppointment(appointment);
                return true;
            }
        }
        return false;
    }

    public static boolean setAttendedAppointment(HttpServletRequest request) {
        Integer appointmentId = 0;
        AppointmentService service = AppointmentUtil.getAppointmentService();
        if (request.getParameter("appointmentId") != null && !request.getParameter("appointmentId").equalsIgnoreCase("")) {
            appointmentId = Integer.valueOf(request.getParameter("appointmentId"));
            Appointment appointment = service.getAppointmentById(appointmentId.intValue());
            if (request.getParameter("attended") != null && request.getParameter("attended").equals("true")) {
                appointment.setAttended(Boolean.valueOf(true));
                appointment.setAppointmentState(new AppointmentState(Integer.valueOf(9), "ATTENDED"));
                service.saveAppointment(appointment);
                System.out.println("_________CANCELED APPOINTMENT__________\n" + appointment.toString());
                return true;
            }
        }
        return false;
    }

    public static Date getPatientLastVisitDate(Patient patient) {
        List encList = Context.getEncounterService().getEncountersByPatientId(patient.getPatientId());
        Date maxDate = ((Encounter)encList.get(0)).getEncounterDatetime();
        for (Encounter enc : Context.getEncounterService().getEncountersByPatientId(patient.getPatientId())) {
            if (enc.getEncounterDatetime().compareTo(maxDate) <= 0) continue;
            maxDate = enc.getEncounterDatetime();
        }
        return maxDate;
    }

    public static List<Appointment> getTodayAppointmentsForProvider(User authUser, Date startDate, Date endDate, Services selectedService) {
        List appointments = new ArrayList<Appointment>();
        AppointmentService ias = AppointmentUtil.getAppointmentService();
        List services = null;
        Services servise = null;
        if (authUser.getPerson().getPersonId() > 1) {
            services = (List)ias.getServicesByProvider(authUser.getPerson());
        }
        if (services != null && selectedService == null) {
            if (services.size() > 1) {
                appointments = AppointmentUtil.getNoSelectedService(authUser, startDate, endDate, appointments, ias, services);
            } else if (services.size() == 1) {
                appointments = AppointmentUtil.getWhereProviderWorksInOneService(authUser, startDate, endDate, ias, servise);
            }
        } else if (selectedService != null) {
            appointments = AppointmentUtil.getFilteredBySelectedService(authUser, startDate, endDate, selectedService, ias);
        }
        return appointments;
    }

    private static List<Appointment> getFilteredBySelectedService(User authUser, Date startDate, Date endDate, Services selectedService, AppointmentService ias) {
        //ArrayList appointments2;
        ArrayList appointments2;
        List<Integer> waitingAppointmentIds = new ArrayList<Integer>();
        Object[] arrobject = new Object[8];
        arrobject[3] = startDate;
        arrobject[5] = endDate;
        arrobject[6] = 4;
        arrobject[7] = selectedService.getServiceId();
        Object[] conditionsWaitingAppointment = arrobject;
        log.info((Object)("__________ <<<<<< Inside the Method: >>>>> ____________ The service ID:" + selectedService.getServiceId() + ", called: " + selectedService.getName()));
        if (authUser.getPerson() != null) {
            waitingAppointmentIds = ias.getAppointmentIdsByMulti(conditionsWaitingAppointment, 100);
            ArrayList<Appointment> waitingAppointments = new ArrayList<Appointment>();
            for (Integer appointmentId : waitingAppointmentIds) {
                waitingAppointments.add(ias.getAppointmentById(appointmentId.intValue()));
            }
            appointments2 = waitingAppointments;
        } else {
            appointments2 = new ArrayList<Appointment>();
        }
        return appointments2;
    }

    private static List<Appointment> getWhereProviderWorksInOneService(User authUser, Date startDate, Date endDate, AppointmentService ias, Services servise) {
        if (authUser.getPerson().getPersonId() > 1) {
            servise = ias.getServiceByProvider(authUser.getPerson());
        }
        List<Appointment> appointments = AppointmentUtil.getFilteredBySelectedService(authUser, startDate, endDate, servise, ias);
        return appointments;
    }

    private static List<Appointment> getNoSelectedService(User authUser, Date startDate, Date endDate, List<Appointment> appointments, AppointmentService ias, List<Services> services) {
        List<Integer> waitingAppointmentIds = new ArrayList<Integer>();
        for (Services serv : services) {
            Object[] arrobject = new Object[8];
            arrobject[3] = startDate;
            arrobject[5] = endDate;
            arrobject[6] = 4;
            arrobject[7] = serv.getServiceId();
            Object[] conditionsWaitingAppointment = arrobject;
            log.info((Object)("_________<< I got some services, : >>_________" + serv.getName() + "\n"));
            if (authUser.getPerson() != null) {
                waitingAppointmentIds = ias.getAppointmentIdsByMulti(conditionsWaitingAppointment, 100);
                ArrayList<Appointment> waitingAppointments = new ArrayList<Appointment>();
                for (Integer appointmentId : waitingAppointmentIds) {
                    waitingAppointments.add(ias.getAppointmentById(appointmentId.intValue()));
                }
                if (waitingAppointments == null) continue;
                log.info((Object)("___________________No of Waiting Appointments gotten from service : " + waitingAppointments.size()));
                appointments.addAll(waitingAppointments);
                continue;
            }
            appointments = new ArrayList<Appointment>();
        }
        return appointments;
    }

    public static String getPatientURL(Services service, User provider) {
        GlobalProperty labProperty = Context.getAdministrationService().getGlobalPropertyObject("mohappointment.link.laboratory_link");
        GlobalProperty pharmacyProperty = Context.getAdministrationService().getGlobalPropertyObject("mohappointment.link.pharmacy_link");
        GlobalProperty labConcept = Context.getAdministrationService().getGlobalPropertyObject("mohappointment.concept.laboratory_concept");
        GlobalProperty pharmacyConcept = Context.getAdministrationService().getGlobalPropertyObject("mohappointment.concept.pharmacy_concept");
        if (service != null && service.equals((Object)AppointmentUtil.getServiceByConcept(Context.getConceptService().getConcept(Integer.valueOf(Integer.parseInt(labConcept.getPropertyValue())))))) {
            return labProperty.getPropertyValue();
        }
        if (service != null && service.equals((Object)AppointmentUtil.getServiceByConcept(Context.getConceptService().getConcept(Integer.valueOf(Integer.parseInt(pharmacyConcept.getPropertyValue())))))) {
            return pharmacyProperty.getPropertyValue();
        }
        return "/patientDashboard.form";
    }

    public static void saveWaitingAppointment(Appointment appointment) {
        AppointmentService service = AppointmentUtil.getAppointmentService();
        appointment.setAppointmentState(new AppointmentState(Integer.valueOf(4), "WAITING"));
        service.saveAppointment(appointment);
    }

    public static void saveAttendedAppointment(Appointment appointment) {
        AppointmentService service = AppointmentUtil.getAppointmentService();
        appointment.setAppointmentState(new AppointmentState(Integer.valueOf(9), "ATTENDED"));
        appointment.setAttended(Boolean.valueOf(true));
        service.saveAppointment(appointment);
    }

    public static void saveUpcomingAppointment(Appointment appointment) {
        AppointmentService service = AppointmentUtil.getAppointmentService();
        appointment.setAppointmentState(new AppointmentState(Integer.valueOf(3), "UPCOMING"));
        service.saveAppointment(appointment);
    }

    public static Appointment getWaitingAppointmentById(int id) {
        AppointmentService service = AppointmentUtil.getAppointmentService();
        return service.getAppointmentById(id);
    }

    public static void editServiceProvider(ServiceProviders serviceProvider) {
        AppointmentService service = AppointmentUtil.getAppointmentService();
        service.saveServiceProviders(serviceProvider);
    }

    public static List<Services> getAllServices() {
        AppointmentService services = AppointmentUtil.getAppointmentService();
        return (List)services.getServices();
    }

    public static List<ServiceProviders> getAllServiceProviders() {
        AppointmentService serviceProviders = AppointmentUtil.getAppointmentService();
        return (List)serviceProviders.getServiceProviders();
    }

    public static ServiceProviders getServiceProvidersById(int id) {
        AppointmentService service = AppointmentUtil.getAppointmentService();
        return service.getServiceProviderById(id);
    }

    public static List<Appointment> getAppointmentsByPatientAndDate(Patient patient, Services clinicalService, Date date) {
        ArrayList<Appointment> appointments;
        AppointmentService service = AppointmentUtil.getAppointmentService();
        Object[] arrobject = new Object[8];
        arrobject[0] = patient.getPatientId();
        arrobject[3] = date;
        Object[] conditions = arrobject;
        appointments = new ArrayList<Appointment>();
        if (clinicalService != null) {
            for (Integer id : service.getAppointmentIdsByMulti(conditions, 100)) {
                Appointment app = service.getAppointmentById(id.intValue());
                if (!app.getService().equals((Object)clinicalService)) continue;
                appointments.add(app);
            }
        } else {
            for (Integer id : service.getAppointmentIdsByMulti(conditions, 100)) {
                appointments.add(service.getAppointmentById(id.intValue()));
            }
        }
        return appointments;
    }

    public static Boolean isPatientAlreadyWaitingThere(Patient patient, AppointmentState state, Services service, Date appointmentDate) throws ParseException {
        AppointmentService appointmentService = AppointmentUtil.getAppointmentService();
        if (appointmentService.getAllWaitingAppointmentsByPatient(patient, state, appointmentDate) != null) {
            for (Appointment appointment : appointmentService.getAllWaitingAppointmentsByPatient(patient, state, appointmentDate)) {
                if (!appointment.getService().equals((Object)service)) continue;
                return true;
            }
        }
        return false;
    }

    public static Collection<Appointment> getAllWaitingAppointmentsByPatientAtService(Patient patient, AppointmentState state, Date appointmentDate, Services service) throws ParseException {
        AppointmentService appointmentService = AppointmentUtil.getAppointmentService();
        ArrayList<Appointment> appointments = new ArrayList<Appointment>();
        for (Appointment appointment : appointmentService.getAllWaitingAppointmentsByPatient(patient, state, appointmentDate)) {
            if (appointment.getService().getServiceId().intValue() != service.getServiceId().intValue()) continue;
            appointments.add(appointment);
        }
        return appointments;
    }

    public static void removeServiceProvidersHavingThisService(Services service) {
        for (ServiceProviders serviceProvider : AppointmentUtil.getAllServiceProviders()) {
            if (serviceProvider.getService().getServiceId().intValue() != service.getServiceId().intValue()) continue;
            serviceProvider.setVoided(true);
            serviceProvider.setVoidedDate(new Date());
            serviceProvider.setVoidedBy(Context.getAuthenticatedUser());
            serviceProvider.setVoidedReason("The service associated to this provider is no longer in use");
            AppointmentUtil.getAppointmentService().saveServiceProviders(serviceProvider);
        }
    }

    public static List<Person> getProvidersByService(Services service) {
        if (service != null) {
            ArrayList<Person> providers = new ArrayList<Person>();
            AppointmentService appService = AppointmentUtil.getAppointmentService();
            for (Integer id : appService.getPersonsByService(service)) {
                providers.add(Context.getPersonService().getPerson(id));
            }
            return providers;
        }
        return null;
    }

    public static Boolean alreadyHasAppointmentThere(Patient patient, Date appointmenDate, Services service) {
        for (Appointment app : AppointmentUtil.getAppointmentsByPatientAndDate(patient, service, appointmenDate)) {
            if (app.getAttended().booleanValue()) continue;
            return true;
        }
        return false;
    }

    public static Services getServiceById(Integer serviceId) {
        return AppointmentUtil.getAppointmentService().getServiceById(serviceId);
    }

    public static Location getDefaultLocation() {
        Integer locationId = Integer.parseInt(Context.getAdministrationService().getGlobalProperty("mohtracportal.defaultLocationId"));
        return Context.getLocationService().getLocation(locationId);
    }

    public  static void voidAppointmentByObs(Obs o){
        AppointmentUtil.getAppointmentService().voidAppointmentByObs(o);
    }
}