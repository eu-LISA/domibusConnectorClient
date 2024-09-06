/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.client.schema.validation;

import lombok.Getter;

/**
 * If the schema validation returns result other than okay, a single result per happening must be
 * instantiated. Collected single results must be put into a
 * {@link ValidationResult} object.
 *
 * @author riederb
 */
@Getter
public class SingleValidationResult {
    private final SeverityLevel level;
    private final String result;

    /**
     * Constructor.
     *
     * @param level The severity level of the validation result.
     * @param result The message or description of the validation result.
     */
    public SingleValidationResult(SeverityLevel level, String result) {
        super();
        this.level = level;
        this.result = result;
    }

    public String getSingleResultString() {
        return level.name() + ": " + result;
    }
}
