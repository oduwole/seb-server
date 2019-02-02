/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sebserver.gbl.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityKey {

    @JsonProperty(value = "modelId", required = true)
    public final String modelId;
    @JsonProperty(value = "entityType", required = true)
    public final EntityType entityType;
    @JsonIgnore
    public final boolean isIdPK;

    @JsonCreator
    public EntityKey(
            @JsonProperty(value = "modelId", required = true) final String modelId,
            @JsonProperty(value = "entityType", required = true) final EntityType entityType) {

        this.modelId = modelId;
        this.entityType = entityType;
        this.isIdPK = entityType != EntityType.USER;
    }

    public EntityKey(
            final Long pk,
            final EntityType entityType) {

        this.modelId = String.valueOf(pk);
        this.entityType = entityType;
        this.isIdPK = true;
    }

    public String getModelId() {
        return this.modelId;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.entityType == null) ? 0 : this.entityType.hashCode());
        result = prime * result + ((this.modelId == null) ? 0 : this.modelId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final EntityKey other = (EntityKey) obj;
        if (this.entityType != other.entityType)
            return false;
        if (this.modelId == null) {
            if (other.modelId != null)
                return false;
        } else if (!this.modelId.equals(other.modelId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EntityKey [modelId=" + this.modelId + ", entityType=" + this.entityType + "]";
    }

}