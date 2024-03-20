package eu.domibus.connector.client.schema.validation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.Logger;

/**
 * The result of a schema validation. Contains single results describing the outcome of the validation.
 * 
 * @author riederb
 *
 */
public class ValidationResult {

	private final Set<SingleValidationResult> validationResults = new HashSet<SingleValidationResult>();

	public ValidationResult() {
	}

	
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
		if(iterate(SeverityLevel.FATAL_ERROR)) {
			return SeverityLevel.FATAL_ERROR;
		}
		if(iterate(SeverityLevel.ERROR)) {
			return SeverityLevel.ERROR;
		}
		if(iterate(SeverityLevel.WARNING)) {
			return SeverityLevel.WARNING;
		}
		return null;
	}

	private boolean iterate(SeverityLevel level) {
		Iterator<SingleValidationResult> it1 = validationResults.iterator();
		while (it1.hasNext()) {
			if (it1.next().getLevel() == level)
				return true;
		}

		return false;
	}

	public Set<SingleValidationResult> getValidationResults() {
		return validationResults;
	}

	/**
	 * Prints all results contained. Results are handed over to the LOGGER level INFO.
	 * If no results are present, the LOGGER prints outcome in level DEBUG.
	 * 
	 * @param LOGGER the logger of the calling class. Results are handed over to the LOGGER level INFO.
	 */
	public void printValidationResults(final Logger LOGGER) {
		if(!validationResults.isEmpty()) {
			LOGGER.info("ValidationResults:");
			Iterator<SingleValidationResult> it1 = validationResults.iterator();
			while (it1.hasNext()) {
				LOGGER.info(it1.next().getSingleResultString());
			}
		}else {
			LOGGER.debug("No ValidationResults found! Validation okay.");
		}
	}
}
