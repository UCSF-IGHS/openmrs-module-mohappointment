/**
 *
 */
package org.openmrs.module.mohappointment.impl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.mohappointment.db.AppointmentDAO;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.AppointmentState;
import org.openmrs.module.mohappointment.model.ServiceProviders;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.service.AppointmentService;

/**
 * @author Kamonyo
 *
 */
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentDAO appointmentDAO;

    public AppointmentDAO getAppointmentDAO() {
        return appointmentDAO;
    }

    public void setAppointmentDAO(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    @Override
    public void cancelAppointment(Appointment appointment) {

        appointmentDAO.cancelAppointment(appointment);
    }

    @Override
    public Collection<Appointment> getAllAppointments() {

        return appointmentDAO.getAllAppointments();
    }

    @Override
    public Appointment getAppointmentById(int appointmentId) {

        return appointmentDAO.getAppointmentById(appointmentId);
    }

    @Override
    public List<Integer> getAppointmentIdsByMulti(Object[] conditions, int limit) {

        return appointmentDAO.getAppointmentIdsByMulti(conditions, limit);
    }

    @Override
    public Integer lastAppointmentId() {

        return appointmentDAO.lastAppointmentId();
    }

    @Override
    public void loadAllAppointments() {

        appointmentDAO.loadAllAppointments();
    }

    @Override
    public void saveAppointment(Appointment appointment) {

        appointmentDAO.saveAppointment(appointment);
    }

    @Override
    public void updateAppointment(Appointment appointment) {

        appointmentDAO.updateAppointment(appointment);
    }

    @Override
    public void updateState(Appointment appointment, Integer stateId) {

        appointmentDAO.updateAppointment(appointment);
    }

    @Override
    public Collection<AppointmentState> getAppointmentStates() {

        return appointmentDAO.getAppointmentStates();
    }

    @Override
    public AppointmentState getAppointmentStatesByName(String name) {

        return appointmentDAO.getAppointmentStatesByName(name);
    }

    @Override
    public void saveService(Services service) {
        appointmentDAO.saveService(service);
    }

    @Override
    public void saveServiceProviders(ServiceProviders serviceProvider) {
        appointmentDAO.saveServiceProviders(serviceProvider);
    }

    @Override
    public void updateService(Services service) {
        appointmentDAO.updateService(service);
    }

    @Override
    public void updateServiceProviders(ServiceProviders serviceProvider) {
        appointmentDAO.updateServiceProviders(serviceProvider);
    }

    @Override
    public Collection<Integer> getPersonsByService(Services service) {
        return appointmentDAO.getPersonsByService(service);
    }

    @Override
    public Services getServiceByProvider(Person provider) {
        return appointmentDAO.getServiceByProvider(provider);
    }

    @Override
    public Services getServiceById(Integer serviceId) {
        return appointmentDAO.getServiceById(serviceId);
    }

    @Override
    public Collection<ServiceProviders> getServiceProviders() {
        return appointmentDAO.getServiceProviders();
    }

    @Override
    public Collection<Services> getServices() {
        return appointmentDAO.getServices();
    }

    @Override
    public Collection<Services> getServicesByProvider(Person provider) {
        return appointmentDAO.getServicesByProvider(provider);
    }

    @Override
    public Services getServiceByConcept(Concept concept) {
        return appointmentDAO.getServiceByConcept(concept);
    }

    @Override
    public ServiceProviders getServiceProviderById(int serviceProviderId) {
        return appointmentDAO.getServiceProviderById(serviceProviderId);
    }

    /**
     * @throws ParseException
     * @see AppointmentService#getAllWaitingAppointmentsByPatient(org.openmrs.Patient, AppointmentState)
     */
    @Override
    public Collection<Appointment> getAllWaitingAppointmentsByPatient(
            Patient patient, AppointmentState state, Date appointmentDate) throws ParseException {
        return appointmentDAO
                .getAllWaitingAppointmentsByPatient(patient, state, appointmentDate);
    }

    @Override
    public void voidAppointmentByObs(Obs o) {
        appointmentDAO.voidAppointmentByObs(o);
    }

    @Override
    public List<Appointment> getAppointmentsByCriteria(Map<String, Object> conditions, int startIndex, int limit) {
        return appointmentDAO.getAppointmentsByCriteria(conditions, startIndex, limit);
    }

    @Override
    public long getAppointmentsCountByCriteria(Map<String, Object> conditions) {
        return appointmentDAO.getAppointmentsCountByCriteria(conditions);
    }

    @Override
    public List<Appointment> getAllAppointments(Date forDate) {
        return appointmentDAO.getAllAppointments(forDate);
    }

    @Override
    public List<Appointment> getAppointmentsForService(Services service, Date startDate, Date endDate) {
        return appointmentDAO.getAppointmentsForService(service, startDate, endDate);
    }
}
