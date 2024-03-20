package eu.domibus.connector.client.schema.validation;

/**
 * If the schema validation returns result other than okay, a single result per happening must be instantiated.
 * Collected single results must be put into a {@link eu.domibus.connector.client.schema.validation.ValidationResult} object.
 * 
 * @author riederb
 *
 */
public class SingleValidationResult {

    private final SeverityLevel level;

    private final String result;

    public SingleValidationResult(SeverityLevel level, String result) {
        super();
        this.level = level;
        this.result = result;
    }


    public SeverityLevel getLevel() {
        return level;
    }

    public String getResult() {
        return result;
    }

    public String getSingleResultString() {
        return level.name() + ": " + result;
    }

}
