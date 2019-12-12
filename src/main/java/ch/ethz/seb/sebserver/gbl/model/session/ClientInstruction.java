/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sebserver.gbl.model.session;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sebserver.gbl.api.EntityType;
import ch.ethz.seb.sebserver.gbl.model.Domain;
import ch.ethz.seb.sebserver.gbl.model.Entity;
import ch.ethz.seb.sebserver.gbl.util.Utils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientInstruction implements Entity {

    private static final String ATTR_SEB_INSTRUCTION_NAME = "seb-instruction";

    public enum InstructionType {
        SEB_QUIT
    }

    @JsonProperty(Domain.CLIENT_INSTRUCTION.ATTR_ID)
    public final Long id;

    @JsonProperty(Domain.CLIENT_INSTRUCTION.ATTR_CLIENT_CONNECTION_ID)
    public final Long connectionId;

    @JsonProperty(Domain.CLIENT_INSTRUCTION.ATTR_EXAM_ID)
    public final Long examId;

    @JsonProperty(Domain.CLIENT_INSTRUCTION.ATTR_TYPE)
    public final InstructionType type;

    @JsonProperty(ATTR_SEB_INSTRUCTION_NAME)
    public final SebInstruction sebInstruction;

    @JsonProperty(Domain.CLIENT_INSTRUCTION.ATTR_ACTIVE)
    public final Boolean active;

    @JsonCreator
    protected ClientInstruction(
            final Long id,
            final Long connectionId,
            final Long examId,
            final InstructionType type,
            final Boolean active,
            final Map<String, String> attributes) {

        Objects.requireNonNull(connectionId);
        Objects.requireNonNull(examId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(active);

        this.id = id;
        this.connectionId = connectionId;
        this.examId = examId;
        this.type = type;
        this.sebInstruction = new SebInstruction(type, attributes);
        this.active = active;
    }

    @Override
    public String getModelId() {
        return (this.id != null)
                ? String.valueOf(this.id)
                : null;
    }

    @Override
    public EntityType entityType() {
        return EntityType.CLIENT_INSTRUCTION;
    }

    @Override
    public String getName() {
        return this.type.name();
    }

    public Long getId() {
        return this.id;
    }

    public Long getConnectionId() {
        return this.connectionId;
    }

    public Long getExamId() {
        return this.examId;
    }

    public SebInstruction getSebInstruction() {
        return this.sebInstruction;
    }

    public Boolean getActive() {
        return this.active;
    }

    public static final class SebInstruction {

        private static final String ATTR_INSTRUCTION_NAME = "instruction";

        @JsonProperty(ATTR_INSTRUCTION_NAME)
        public final String instruction;

        @JsonProperty(Domain.CLIENT_INSTRUCTION.ATTR_ATTRIBUTES)
        public final Map<String, String> attributes;

        @JsonCreator
        protected SebInstruction(
                final InstructionType type,
                final Map<String, String> attributes) {

            Objects.requireNonNull(type);

            this.instruction = type.name();
            this.attributes = (attributes != null) ? Utils.immutableMapOf(attributes) : Collections.emptyMap();
        }

        public String getInstruction() {
            return this.instruction;
        }

        public Map<String, String> getAttributes() {
            return this.attributes;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("SebInstruction [instruction=");
            builder.append(this.instruction);
            builder.append(", attributes=");
            builder.append(this.attributes);
            builder.append("]");
            return builder.toString();
        }

    }

}