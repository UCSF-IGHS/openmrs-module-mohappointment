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

import org.openmrs.module.mohappointment.model.ChangedAppointment;
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
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/mohappointment/changedAppointment",
        supportedClass = ChangedAppointment.class,
        supportedOpenmrsVersions = {"2.0 - 9.*"})
public class ChangedAppointmentResource extends DelegatingCrudResource<ChangedAppointment> {

    @Override
    protected String getUniqueId(ChangedAppointment delegate) {
        return String.valueOf(delegate.getChangedAppointId());
    }

    @Override
    public ChangedAppointment getByUniqueId(String s) {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    protected void delete(ChangedAppointment changedAppointment, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public ChangedAppointment newDelegate() {
        return new ChangedAppointment();
    }

    @Override
    public ChangedAppointment save(ChangedAppointment changedAppointment) {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public void purge(ChangedAppointment changedAppointment, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("appointment");
        description.addProperty("reason");
        description.addProperty("newDateOfAppointment");
        return description;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        DelegatingResourceDescription description = null;

        if (representation instanceof RefRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("changedAppointId");
            description.addProperty("appointment", Representation.REF);
            description.addProperty("reason");
            description.addProperty("newDateOfAppointment");
            description.addSelfLink();
        } else if (representation instanceof DefaultRepresentation || representation instanceof FullRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("changedAppointId");
            description.addProperty("appointment");
            description.addProperty("reason");
            description.addProperty("newDateOfAppointment");
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
        throw new ResourceDoesNotSupportOperationException();
    }
}
