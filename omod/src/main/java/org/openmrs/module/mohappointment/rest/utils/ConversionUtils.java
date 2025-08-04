/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mohappointment.rest.utils;

import java.util.stream.Collectors;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.webservices.rest.SimpleObject;

public class ConversionUtils {

    /**
     * Converts an Appointment object to a SimpleObject representation.
     *
     * @param appointment the Appointment object to convert
     * @return a SimpleObject representation of the appointment
     */
    public static SimpleObject appointmentToSimpleObject(Appointment appointment) {
        SimpleObject obj = new SimpleObject();
        obj.add("appointmentId", appointment.getAppointmentId());
        obj.add("appointmentDate", String.valueOf(appointment.getAppointmentDate()));
        obj.add("reason", obsToSimpleObject(appointment.getReason()));
        obj.add("nextVisitDate", obsToSimpleObject(appointment.getNextVisitDate()));
        obj.add("note", appointment.getNote());
        obj.add("encounter", encounterToSimpleObject(appointment.getEncounter()));
        obj.add("location", locationToSimpleObject(appointment.getLocation()));
        obj.add("provider", personToSimpleObject(appointment.getProvider()));
        obj.add("service", serviceToSimpleObject(appointment.getService()));
        obj.add("appointmentService", serviceToSimpleObject(appointment.getService()));
        obj.add("patient", patientToSimpleObject(appointment.getPatient()));
        obj.add("appointmentState", appointment.getAppointmentState().getDescription());
        obj.add("attended", appointment.getAttended());
        obj.add("voided", appointment.isVoided());
        obj.add("voidReason", appointment.getVoidReason());
        obj.add("voidedDate", appointment.getVoidedDate());
        obj.add("creator", userToSimpleObject(appointment.getCreator()));
        obj.add("voidedBy", userToSimpleObject(appointment.getVoidedBy()));
        obj.add("createdDate", appointment.getCreatedDate());
        return obj;
    }

    /**
     * Converts an Obs object to a SimpleObject representation.
     *
     * @param obs the Obs object to convert
     * @return a SimpleObject representation of the obs
     */
    public static SimpleObject obsToSimpleObject(org.openmrs.Obs obs) {
        if (obs == null) return null;
        SimpleObject obj = new SimpleObject();
        obj.add("obsId", obs.getObsId());
        obj.add("uuid", obs.getUuid());
        obj.add("concept", conceptToSimpleObject(obs.getConcept()));
        obj.add("value", obs.getValueCoded() != null ? conceptToSimpleObject(obs.getValueCoded()) : obs.getValueAsString(Context.getLocale()));
        obj.add("obsDatetime", obs.getObsDatetime());
        obj.add("person", personToSimpleObject(obs.getPerson()));
        return obj;
    }

    /**
     * Converts a Concept object to a SimpleObject representation.
     *
     * @param concept the Concept object to convert
     * @return a SimpleObject representation of the concept
     */
    public static SimpleObject conceptToSimpleObject(Concept concept) {
        if (concept == null) return null;
        SimpleObject obj = new SimpleObject();
        obj.add("conceptId", concept.getConceptId());
        obj.add("uuid", concept.getUuid());
        obj.add("display", concept.getDisplayString());
        obj.add("name", concept.getName() != null ? concept.getName().getName() : null);
        return obj;
    }

    /**
     * Converts a Services object to a SimpleObject representation.
     *
     * @param service the Services object to convert
     * @return a SimpleObject representation of the service
     */
    public static SimpleObject serviceToSimpleObject(Services service) {
        if (service == null) return null;
        SimpleObject obj = new SimpleObject();
        obj.add("serviceId", service.getServiceId());
        obj.add("uuid", service.getServiceId());
        obj.add("name", service.getName());
        obj.add("description", service.getDescription());
        obj.add("concept", conceptToSimpleObject(service.getConcept()));
        return obj;
    }

    /**
     * Converts an Encounter object to a SimpleObject representation.
     *
     * @param encounter the Encounter object to convert
     * @return a SimpleObject representation of the encounter
     */
    public static SimpleObject encounterToSimpleObject(org.openmrs.Encounter encounter) {
        if (encounter == null) return null;
        SimpleObject obj = new SimpleObject();
        obj.add("encounterId", encounter.getEncounterId());
        obj.add("uuid", encounter.getUuid());
        obj.add("encounterDatetime", encounter.getEncounterDatetime());
        obj.add("encounterType", encounter.getEncounterType() != null ? encounter.getEncounterType().getName() : null);
        obj.add("location", locationToSimpleObject(encounter.getLocation()));
        obj.add("provider", encounter.getEncounterProviders() != null && !encounter.getEncounterProviders().isEmpty() ?
                personToSimpleObject(encounter.getEncounterProviders().iterator().next().getProvider().getPerson()) : null);
        return obj;
    }

    /**
     * Converts a Location object to a SimpleObject representation.
     *
     * @param location the Location object to convert
     * @return a SimpleObject representation of the location
     */
    public static SimpleObject locationToSimpleObject(org.openmrs.Location location) {
        if (location == null) return null;
        SimpleObject obj = new SimpleObject();
        obj.add("locationId", location.getLocationId());
        obj.add("uuid", location.getUuid());
        obj.add("name", location.getName());
        obj.add("description", location.getDescription());
        return obj;
    }

    /**
     * Converts a Person object to a SimpleObject representation.
     *
     * @param person the Person object to convert
     * @return a SimpleObject representation of the person
     */
    public static SimpleObject personToSimpleObject(org.openmrs.Person person) {
        if (person == null) return null;
        SimpleObject obj = new SimpleObject();
        obj.add("personId", person.getPersonId());
        obj.add("uuid", person.getUuid());
        obj.add("gender", person.getGender());
        obj.add("birthdate", person.getBirthdate());
        obj.add("age", person.getAge());
        obj.add("names", person.getNames() != null ?
                person.getNames().stream().map(n -> n.getFullName()).collect(Collectors.toList()) : null);
        return obj;
    }

    /**
     * Converts a Patient object to a SimpleObject representation.
     *
     * @param patient the Patient object to convert
     * @return a SimpleObject representation of the patient
     */
    public static SimpleObject patientToSimpleObject(org.openmrs.Patient patient) {
        if (patient == null) return null;
        SimpleObject obj = new SimpleObject();
        obj.add("patientId", patient.getPatientId());
        obj.add("uuid", patient.getUuid());
        obj.add("name", patient.getPerson().getPersonName().getFullName());
        obj.add("person", personToSimpleObject(patient));
        obj.add("identifiers", patient.getActiveIdentifiers() != null ?
                patient.getActiveIdentifiers().stream().map(id -> {
                    SimpleObject idObj = new SimpleObject();
                    idObj.add("identifier", id.getIdentifier());
                    idObj.add("identifierType", id.getIdentifierType() != null ? id.getIdentifierType().getName() : null);
                    return idObj;
                }).collect(Collectors.toList()) : null);
        return obj;
    }

    /**
     * Converts a User object to a SimpleObject representation.
     *
     * @param user the User object to convert
     * @return a SimpleObject representation of the user
     */
    public static SimpleObject userToSimpleObject(org.openmrs.User user) {
        if (user == null) return null;
        SimpleObject obj = new SimpleObject();
        obj.add("uuid", user.getUuid());
        obj.add("username", user.getUsername());
        obj.add("person", personToSimpleObject(user.getPerson()));
        return obj;
    }
}
