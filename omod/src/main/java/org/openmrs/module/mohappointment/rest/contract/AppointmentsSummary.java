/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mohappointment.rest.contract;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openmrs.module.webservices.rest.SimpleObject;

public class AppointmentsSummary {
    private SimpleObject appointmentService;
    private Map<String, DailyAppointmentServiceSummary> appointmentCountMap;

    @JsonCreator
    public AppointmentsSummary(@JsonProperty("appointmentService") SimpleObject appointmentService,
                               @JsonProperty("appointmentCountMap") Map appointmentCountMap) {
        this.appointmentService = appointmentService;
        this.appointmentCountMap = appointmentCountMap;
    }

    public SimpleObject getAppointmentService() {
        return appointmentService;
    }

    public void setAppointmentService(SimpleObject appointmentService) {
        this.appointmentService = appointmentService;
    }

    public Map<String, DailyAppointmentServiceSummary> getAppointmentCountMap() {
        return appointmentCountMap;
    }

    public void setAppointmentCountMap(Map<String, DailyAppointmentServiceSummary> appointmentCountMap) {
        this.appointmentCountMap = appointmentCountMap;
    }
}
