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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.service.AppointmentService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

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
        return description;
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
        String patientUuid = context.getRequest().getParameter("patient");
        String providerUuid = context.getRequest().getParameter("provider");
        String locationUuid = context.getRequest().getParameter("location");
        String appointmentDate = context.getRequest().getParameter("appointmentDate");
        String attended = context.getRequest().getParameter("attended");
        List<Appointment> appointments = new ArrayList<>();
        Object[] conditions = new Object[8];

        if (patientUuid != null) {
            conditions[0] = Context.getPatientService().getPatientByUuid(patientUuid).getPatientId();
        }

        if (providerUuid != null) {
            conditions[1] = Context.getUserService().getUsersByPerson(Context.getPersonService().getPersonByUuid(providerUuid), false).get(0).getUserId();
        }

        if (locationUuid != null) {
            conditions[2] = Context.getLocationService().getLocationByUuid(locationUuid).getLocationId();
        }

        if (appointmentDate != null) {
            try {
                conditions[3] = Context.getDateFormat().parse(appointmentDate);
                conditions[5] = Context.getDateFormat().parse(appointmentDate);
            } catch (Exception e) {

            }
        }

        if (attended != null) {
            conditions[4] = Boolean.parseBoolean(attended);
        }

        List<Integer> appointmentIdsByMulti = Context.getService(AppointmentService.class).getAppointmentIdsByMulti(conditions, context.getLimit());
        if (appointmentIdsByMulti != null && !appointmentIdsByMulti.isEmpty()) {
            appointmentIdsByMulti.forEach(appointmentId -> appointments.add(Context.getService(AppointmentService.class).getAppointmentById(appointmentId)));
        }

        return new NeedsPaging<>(appointments, context);
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        List<Appointment> appointments = new ArrayList<>(Context.getService(AppointmentService.class).getAllAppointments());
        return new NeedsPaging<>(appointments, context);
    }
}
