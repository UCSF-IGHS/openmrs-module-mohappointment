/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mohappointment.rest.controller;

import static org.openmrs.module.mohappointment.rest.utils.ConversionUtils.appointmentToSimpleObject;
import static org.openmrs.module.mohappointment.rest.utils.ConversionUtils.serviceToSimpleObject;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.rest.contract.AppointmentsSummary;
import org.openmrs.module.mohappointment.rest.contract.DailyAppointmentServiceSummary;
import org.openmrs.module.mohappointment.service.AppointmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/mohappointment")
public class AppointmentController extends MainResourceController {
    @Override
    public String getNamespace() {
        return "v1/mohappointment";
    }

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @RequestMapping(method = RequestMethod.GET, value = "all")
    @ResponseBody
    public List<SimpleObject> getAllAppointments(@RequestParam(value = "forDate", required = false) String forDate) throws ParseException {
        List<Appointment> appointments = Context.getService(AppointmentService.class).getAllAppointments(simpleDateFormat.parse(forDate));
        return appointments.stream().map(appointment -> appointmentToSimpleObject(appointment)).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.POST, value = "search")
    @ResponseBody
    public List<SimpleObject> searchAppointments(@Valid @RequestBody SimpleObject simpleObject) throws IOException {
        List<Appointment> appointments;

        try {
            Date startDate = simpleDateFormat.parse(simpleObject.get("startDate").toString());
            Date endDate = simpleDateFormat.parse(simpleObject.get("endDate").toString());
            appointments = Context.getService(AppointmentService.class).getAppointmentsForService(null, startDate, endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return appointments.stream().map(appointment -> appointmentToSimpleObject(appointment)).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "appointmentSummary")
    @ResponseBody
    public List<AppointmentsSummary> getAllAppointmentsSummary(@RequestParam(value = "startDate") String startDateString, @RequestParam(value = "endDate") String endDateString) throws ParseException {
        List<AppointmentsSummary> appointmentsSummaryList = new ArrayList<>();
        Date startDate = simpleDateFormat.parse(startDateString);
        Date endDate = simpleDateFormat.parse(endDateString);

        AppointmentService appointmentService = Context.getService(AppointmentService.class);

        List<Services> servicesList = new ArrayList<>(appointmentService.getServices());
        for (Services service : servicesList) {
            List<Appointment> appointmentsForService = appointmentService.getAppointmentsForService(service, startDate, endDate);

            Map<Date, List<Appointment>> appointmentsGroupedByDate =
                    appointmentsForService.stream().collect(Collectors.groupingBy(appointment ->
                            DateUtils.truncate(appointment.getAppointmentDate(), java.util.Calendar.DAY_OF_MONTH)));

            Map<String, DailyAppointmentServiceSummary> appointmentCountMap = new LinkedHashMap<>();
            for (Map.Entry<Date, List<Appointment>> appointmentDateMap : appointmentsGroupedByDate.entrySet()) {
                List<Appointment> appointments = appointmentDateMap.getValue();
                Long missedAppointmentsCount = appointments.stream().filter(
                        appointment -> appointment.getAttended().equals(false) && appointment.getAppointmentDate().before(new Date())).count();
                DailyAppointmentServiceSummary dailyAppointmentServiceSummary = new DailyAppointmentServiceSummary(
                        appointmentDateMap.getKey(), String.valueOf(service.getServiceId()), appointments.size(), Math.toIntExact(missedAppointmentsCount));
                appointmentCountMap.put(simpleDateFormat.format(appointmentDateMap.getKey()), dailyAppointmentServiceSummary);
            }

            AppointmentsSummary appointmentsSummary = new AppointmentsSummary(serviceToSimpleObject(service), appointmentCountMap);
            appointmentsSummaryList.add(appointmentsSummary);
        }
        return appointmentsSummaryList;
    }
}
