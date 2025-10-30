/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mohappointment.rest.resource;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.*;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.AppointmentState;
import org.openmrs.module.mohappointment.service.AppointmentService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.openmrs.module.mohappointment.utils.ConstantValues.*;

@Resource(name = RestConstants.VERSION_1 + "/mohappointment/appointment",
        supportedClass = Appointment.class,
        supportedOpenmrsVersions = {"2.0 - 9.*"})
public class AppointmentResource extends DelegatingCrudResource<Appointment> {

    @Override
    protected String getUniqueId(Appointment delegate) {
        return String.valueOf(delegate.getAppointmentId());
    }

    @Override
    public Appointment getByUniqueId(String s) {
        return Context.getService(AppointmentService.class).getAppointmentById(Integer.parseInt(s));
    }

    @Override
    protected void delete(Appointment appointment, String s, RequestContext requestContext) throws ResponseException {
        // Soft delete (void) the appointment
        appointment.setVoided(true);
        appointment.setVoidedBy(Context.getAuthenticatedUser());
        appointment.setVoidReason(s);
        Context.getService(AppointmentService.class).saveAppointment(appointment);
    }

    @Override
    public Appointment newDelegate() {
        return new Appointment();
    }

    @Override
    public Appointment save(Appointment appointment) {
        if (appointment.getCreator() == null) {
            appointment.setCreator(Context.getAuthenticatedUser());
        }

        if (appointment.getCreatedDate() == null) {
            appointment.setCreatedDate(new Date());
        }

        if (appointment.getAppointmentDate() != null && appointment.getAppointmentDate().after(new Date())) {
            log.info("Appointment date is set in the future: " + appointment.getAppointmentDate());
            appointment.setAppointmentState(new AppointmentState(3, "UPCOMING"));
        } else {
            log.info("Appointment date is not set in the future: " + appointment.getAppointmentDate());
        }

        Context.getService(AppointmentService.class).saveAppointment(appointment);
        return appointment;
    }

    @Override
    public void purge(Appointment appointment, RequestContext requestContext) throws ResponseException {
        appointment.setVoided(true);
        appointment.setVoidedBy(Context.getAuthenticatedUser());
        appointment.setVoidReason("Purged by REST API");
        Context.getService(AppointmentService.class).saveAppointment(appointment);
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("appointmentDate");
        description.addProperty("reason");
        description.addProperty("nextVisitDate");
        description.addProperty("note");
        description.addProperty("encounter");
        description.addProperty("location");
        description.addProperty("provider");
        description.addProperty("service");
        description.addProperty("patient");
        description.addProperty("appointmentState");
        return description;
    }

    @Override
    public Model getGETModel(Representation rep) {
        ModelImpl model = (ModelImpl) super.getGETModel(rep);
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            model
                    .property("appointmentId", new IntegerProperty())
                    .property("appointmentDate", new DateTimeProperty())
                    .property("reason", new RefProperty("#/definitions/ObsGet"))
                    .property("nextVisitDate", new RefProperty("#/definitions/ObsGet"))
                    .property("note", new StringProperty())
                    .property("encounter", new RefProperty("#/definitions/EncounterGet"))
                    .property("location", new RefProperty("#/definitions/LocationGet"))
                    .property("provider", new RefProperty("#/definitions/PersonGet"))
                    .property("service", new RefProperty("#/definitions/MohappointmentServicesGet"))
                    .property("patient", new RefProperty("#/definitions/PatientGet"))
                    .property("appointmentState", new RefProperty("#/definitions/MohappointmentAppointmentStateGet"));
        }
        if (rep instanceof FullRepresentation) {
            model
                    .property("creator", new RefProperty("#/definitions/UserGet"))
                    .property("createdDate", new DateTimeProperty())
                    .property("voided", new BooleanProperty())
                    .property("voidedBy", new RefProperty("#/definitions/UserGet"))
                    .property("voidReason", new StringProperty())
                    .property("voidedDate", new DateTimeProperty());
        }
        return model;
    }

    @Override
    public Model getCREATEModel(Representation rep) {
        ModelImpl model = new ModelImpl()
                .property("appointmentDate", new DateTimeProperty()
                        .example("2024-01-15T10:30:00.000")
                        .description("Date and time of the appointment"))
                .property("reason", new ObjectProperty()
                        .description("Obs object for reason"))
                .property("nextVisitDate", new ObjectProperty()
                        .description("Obs object for next visit date"))
                .property("note", new StringProperty()
                        .description("Additional notes"))
                .property("encounter", new ObjectProperty()
                        .property("uuid", new StringProperty())
                        .description("Encounter object identified by uuid"))
                .property("location", new ObjectProperty()
                        .property("uuid", new StringProperty())
                        .description("Location object identified by uuid"))
                .property("provider", new ObjectProperty()
                        .property("uuid", new StringProperty())
                        .description("Provider (Person) object identified by uuid"))
                .property("service", new ObjectProperty()
                        .property("serviceId", new IntegerProperty())
                        .description("Service object identified by serviceId"))
                .property("patient", new ObjectProperty()
                        .property("uuid", new StringProperty())
                        .description("Patient object identified by uuid"))
                .property("appointmentState", new ObjectProperty()
                        .property("appointmentStateId", new IntegerProperty())
                        .description("Appointment State object identified by appointmentStateId"));

        return model;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        DelegatingResourceDescription description = null;

        if (representation instanceof RefRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("appointmentId");
            description.addProperty("appointmentDate");
            description.addProperty("reason", Representation.REF);
            description.addProperty("nextVisitDate", Representation.REF);
            description.addProperty("note");
            description.addProperty("encounter", Representation.REF);
            description.addProperty("location", Representation.REF);
            description.addProperty("provider", Representation.REF);
            description.addProperty("service", Representation.REF);
            description.addProperty("patient", Representation.REF);
            description.addProperty("appointmentState", Representation.REF);
            description.addSelfLink();
        } else if (representation instanceof DefaultRepresentation || representation instanceof FullRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("appointmentId");
            description.addProperty("appointmentDate");
            description.addProperty("reason");
            description.addProperty("nextVisitDate");
            description.addProperty("note");
            description.addProperty("encounter");
            description.addProperty("location");
            description.addProperty("provider");
            description.addProperty("service");
            description.addProperty("patient");
            description.addProperty("appointmentState");
            description.addSelfLink();
            if (representation instanceof DefaultRepresentation) {
                description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            }
        }
        return description;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        String patientUuid = context.getRequest().getParameter(REQUEST_PARAMETER_PATIENT);
        String providerUuid = context.getRequest().getParameter(REQUEST_PARAMETER_PROVIDER);
        String locationUuid = context.getRequest().getParameter(REQUEST_PARAMETER_LOCATION);
        String appointmentDate = context.getRequest().getParameter(REQUEST_PARAMETER_APPOINTMENT_DATE);
        String attended = context.getRequest().getParameter(REQUEST_PARAMETER_ATTENDED);
        String appointmentState = context.getRequest().getParameter(REQUEST_PARAMETER_APPOINTMENT_STATE);
        Map<String, Object> conditions = new HashMap<>();

        if (patientUuid != null) {
            conditions.put(REQUEST_PARAMETER_PATIENT, Context.getPatientService().getPatientByUuid(patientUuid));
        }

        if (providerUuid != null) {
            conditions.put(REQUEST_PARAMETER_PROVIDER, Context.getPersonService().getPersonByUuid(providerUuid));
        }

        if (locationUuid != null) {
            conditions.put(REQUEST_PARAMETER_LOCATION, Context.getLocationService().getLocationByUuid(locationUuid));
        }

        if (appointmentDate != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                conditions.put(REQUEST_PARAMETER_APPOINTMENT_DATE, sdf.parse(appointmentDate));
            } catch (Exception e) {
                log.error("Error parsing date: " + e.getMessage(), e);
            }
        }

        if (attended != null) {
            conditions.put(REQUEST_PARAMETER_ATTENDED, Boolean.parseBoolean(attended));
        }

        if (appointmentState != null) {
            AppointmentState state = Context.getService(AppointmentService.class).getAppointmentStatesByName(appointmentState);
            if (state == null) {
                state = Context.getService(AppointmentService.class).getAppointmentStates().stream()
                        .filter(s -> s.getAppointmentStateId().equals(appointmentState))
                        .findFirst()
                        .orElse(null);
            }

            if (state != null) {
                conditions.put(REQUEST_PARAMETER_APPOINTMENT_STATE, state);
            }
        }

        int startIndex = context.getStartIndex();
        int limit = context.getLimit();

        List<Appointment> appointments = Context.getService(AppointmentService.class).getAppointmentsByCriteria(conditions, startIndex, limit);
        long totalCount = Context.getService(AppointmentService.class).getAppointmentsCountByCriteria(conditions);
        boolean hasMore = (startIndex + limit) < totalCount;

        return new AlreadyPaged<>(context, appointments, hasMore, totalCount);
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        List<Appointment> appointments = new ArrayList<>(Context.getService(AppointmentService.class).getAllAppointments());
        return new NeedsPaging<>(appointments, context);
    }

    @PropertySetter("reason")
    public void setReason(Appointment appointment, Obs reason) {
        appointment.setReason(Context.getObsService().saveObs(reason, null));
    }
}
