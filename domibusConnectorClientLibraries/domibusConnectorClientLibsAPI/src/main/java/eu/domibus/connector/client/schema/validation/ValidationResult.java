/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.schema.validation;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Logger;

/**
 * The result of a schema validation. Contains single results describing the outcome of the
 * validation.
 *
 * @author riederb
 */
@NoArgsConstructor
@Getter
public class ValidationResult {
    private final Set<SingleValidationResult> validationResults = new HashSet<>();

    /**
     * Checks, if the schema validation has any outcome.
     *
     * @return true, if no single result is present.
     */
    public boolean isOkay() {
        return validationResults.isEmpty();
    }

    /**
     * Checks, if a single result of severity FATAL_ERROR is present.
     *
     * @return true, if a single result of severity level FATAL_ERROR is present.
     */
    public boolean isFatal() {
        return iterate(SeverityLevel.FATAL_ERROR);
    }

    /**
     * Checks, if a single result of severity ERROR is present.
     *
     * @return true, if a single result of severity level ERROR is present.
     */
    public boolean isError() {
        return iterate(SeverityLevel.ERROR);
    }

    /**
     * Checks, if a single result of severity WARNING is present.
     *
     * @return true, if a single result of severity level WARNING is present.
     */
    public boolean isWarning() {
        return iterate(SeverityLevel.WARNING);
    }

    /**
     * Iterates single results and returns the highest severity level present.
     *
     * @return the highest severity level contained in a single result.
     */
    public SeverityLevel maxSeverityLevel() {
        if (iterate(SeverityLevel.FATAL_ERROR)) {
            return SeverityLevel.FATAL_ERROR;
        }
        if (iterate(SeverityLevel.ERROR)) {
            return SeverityLevel.ERROR;
        }
        if (iterate(SeverityLevel.WARNING)) {
            return SeverityLevel.WARNING;
        }
        return null;
    }

    private boolean iterate(SeverityLevel level) {
        for (SingleValidationResult validationResult : validationResults) {
            if (validationResult.getLevel() == level) {
                return true;
            }
        }

        return false;
    }

    /**
     * Prints all results contained. Results are handed over to the LOGGER level INFO. If no results
     * are present, the LOGGER prints outcome in level DEBUG.
     *
     * @param logger the logger of the calling class. Results are handed over to the LOGGER level
     *               INFO.
     */
    public void printValidationResults(final Logger logger) {
        if (!validationResults.isEmpty()) {
            logger.info("ValidationResults:");
            for (SingleValidationResult validationResult : validationResults) {
                logger.info(validationResult.getSingleResultString());
            }
        } else {
            logger.debug("No ValidationResults found! Validation okay.");
        }
    }
}
