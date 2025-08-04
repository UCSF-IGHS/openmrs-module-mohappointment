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

import java.util.Date;

public class DailyAppointmentServiceSummary {
    private Integer allAppointmentsCount;
    private Integer missedAppointmentsCount;
    private Date appointmentDate;
    private String appointmentServiceUuid;

    public DailyAppointmentServiceSummary(Date appointmentDate, String appointmentServiceUuid, Integer allAppointmentsCount, Integer missedAppointmentsCount) {
        this.allAppointmentsCount = allAppointmentsCount;
        this.missedAppointmentsCount = missedAppointmentsCount;
        this.appointmentDate = appointmentDate;
        this.appointmentServiceUuid = appointmentServiceUuid;
    }

    public Integer getAllAppointmentsCount() {
        return allAppointmentsCount;
    }

    public void setAllAppointmentsCount(Integer allAppointmentsCount) {
        this.allAppointmentsCount = allAppointmentsCount;
    }

    public Integer getMissedAppointmentsCount() {
        return missedAppointmentsCount;
    }

    public void setMissedAppointmentsCount(Integer missedAppointmentsCount) {
        this.missedAppointmentsCount = missedAppointmentsCount;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentServiceUuid() {
        return appointmentServiceUuid;
    }

    public void setAppointmentServiceUuid(String appointmentServiceUuid) {
        this.appointmentServiceUuid = appointmentServiceUuid;
    }
}
