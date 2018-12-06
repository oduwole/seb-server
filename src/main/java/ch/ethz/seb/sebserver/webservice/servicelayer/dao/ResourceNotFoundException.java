/*
 * Copyright (c) 2018 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sebserver.webservice.servicelayer.dao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.ethz.seb.sebserver.gbl.model.EntityType;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8319235723086949618L;

    public final EntityType entityType;
    public final String entityId;

    public ResourceNotFoundException(final EntityType entityType, final String entityId) {
        super("Resource " + entityType + " with ID: " + entityId + " not found");
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public ResourceNotFoundException(final EntityType entityType, final String entityId, final Throwable cause) {
        super("Resource " + entityType + " with ID: " + entityId + " not found", cause);
        this.entityType = entityType;
        this.entityId = entityId;
    }

}
