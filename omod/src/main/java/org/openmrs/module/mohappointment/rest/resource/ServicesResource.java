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

import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.Services;
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

@Resource(name = RestConstants.VERSION_1 + "/mohappointment/services",
        supportedClass = Services.class,
        supportedOpenmrsVersions = {"2.0 - 9.*"})
public class ServicesResource extends DelegatingCrudResource<Services> {

    @Override
    protected String getUniqueId(Services delegate) {
        return String.valueOf(delegate.getServiceId());
    }

    @Override
    public Services getByUniqueId(String s) {
        return Context.getService(AppointmentService.class).getServiceById(Integer.parseInt(s));
    }

    @Override
    protected void delete(Services services, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public Services newDelegate() {
        return new Services();
    }

    @Override
    public Services save(Services services) {
        if (services.getCreator() == null) {
            services.setCreator(Context.getAuthenticatedUser());
        }

        if (services.getCreatedDate() == null) {
            services.setCreatedDate(new Date());
        }
        Context.getService(AppointmentService.class).saveService(services);
        return services;
    }

    @Override
    public void purge(Services services, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("name");
        description.addProperty("description");
        description.addProperty("concept");
        return description;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        DelegatingResourceDescription description = null;

        if (representation instanceof RefRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("serviceId");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("concept", Representation.REF);
            description.addSelfLink();
        } else if (representation instanceof DefaultRepresentation || representation instanceof FullRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("serviceId");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("concept");
            description.addSelfLink();
            if (representation instanceof DefaultRepresentation) {
                description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            }
        }
        return description;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        return super.doSearch(context);
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<Services>(new ArrayList<>(Context.getService(AppointmentService.class).getServices()), context);
    }
}
