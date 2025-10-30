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
import org.openmrs.api.context.Context;
import org.openmrs.module.mohappointment.model.ServiceProviders;
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

import java.util.ArrayList;
import java.util.Date;

@Resource(name = RestConstants.VERSION_1 + "/mohappointment/serviceProviders",
        supportedClass = ServiceProviders.class,
        supportedOpenmrsVersions = {"2.0 - 9.*"})
public class ServiceProvidersResource extends DelegatingCrudResource<ServiceProviders> {
    @Override
    protected String getUniqueId(ServiceProviders delegate) {
        return String.valueOf(delegate.getServiceProviderId());
    }

    @Override
    public ServiceProviders getByUniqueId(String s) {
        return Context.getService(AppointmentService.class).getServiceProviderById(Integer.parseInt(s));
    }

    @Override
    protected void delete(ServiceProviders serviceProviders, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public ServiceProviders newDelegate() {
        return new ServiceProviders();
    }

    @Override
    public ServiceProviders save(ServiceProviders serviceProviders) {
        if (serviceProviders.getCreator() == null) {
            serviceProviders.setCreator(Context.getAuthenticatedUser());
        }

        if (serviceProviders.getCreatedDate() == null) {
            serviceProviders.setCreatedDate(new Date());
        }

        Context.getService(AppointmentService.class).saveServiceProviders(serviceProviders);
        return serviceProviders;
    }

    @Override
    public void purge(ServiceProviders serviceProviders, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("startDate");
        description.addProperty("provider");
        description.addProperty("service");
        description.addProperty("names");
        return description;
    }

    @Override
    public Model getGETModel(Representation rep) {
        ModelImpl model = (ModelImpl) super.getGETModel(rep);
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            model
                    .property("serviceProviderId", new IntegerProperty())
                    .property("startDate", new DateTimeProperty())
                    .property("provider", new RefProperty("#/definitions/PersonGet"))
                    .property("service", new RefProperty("#/definitions/MohappointmentServicesGet"))
                    .property("names", new StringProperty());
        }
        if (rep instanceof FullRepresentation) {
            model
                    .property("creator", new RefProperty("#/definitions/UserGet"))
                    .property("createdDate", new DateTimeProperty())
                    .property("voided", new BooleanProperty())
                    .property("voidedBy", new RefProperty("#/definitions/UserGet"))
                    .property("voidedReason", new StringProperty())
                    .property("voidedDate", new DateTimeProperty());
        }
        return model;
    }

    @Override
    public Model getCREATEModel(Representation rep) {
        ModelImpl model = new ModelImpl()
                .property("startDate", new DateTimeProperty()
                        .example("2024-01-01T08:00:00.000")
                        .description("Start date for the service provider"))
                .property("provider", new ObjectProperty()
                        .property("uuid", new StringProperty())
                        .description("Provider (Person) object identified by uuid"))
                .property("service", new ObjectProperty()
                        .property("serviceId", new IntegerProperty())
                        .description("Service object identified by serviceId"))
                .property("names", new StringProperty()
                        .description("Provider names"));

        model.required("startDate")
                .required("provider")
                .required("service");

        return model;
    }

    @Override
    public Model getUPDATEModel(Representation rep) {
        return getCREATEModel(rep);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        DelegatingResourceDescription description = null;

        if (representation instanceof RefRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("serviceProviderId");
            description.addProperty("startDate");
            description.addProperty("provider", Representation.REF);
            description.addProperty("service", Representation.REF);
            description.addProperty("names");
            description.addSelfLink();
        } else if (representation instanceof DefaultRepresentation || representation instanceof FullRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("serviceProviderId");
            description.addProperty("startDate");
            description.addProperty("provider");
            description.addProperty("service");
            description.addProperty("names");
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
        return new NeedsPaging<ServiceProviders>(new ArrayList<>(Context.getService(AppointmentService.class).getServiceProviders()), context);
    }
}
