package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Error response model for validation and other errors
 */
public class ErrorResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("errors")
    private Map<String, List<String>> errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }

    /**
     * Get the first error message from errors map
     */
    public String getFirstError() {
        if (errors != null && !errors.isEmpty()) {
            for (List<String> errorList : errors.values()) {
                if (errorList != null && !errorList.isEmpty()) {
                    return errorList.get(0);
                }
            }
        }
        return message != null ? message : "An error occurred";
    }

    /**
     * Get all errors as a single string
     */
    public String getAllErrors() {
        if (errors == null || errors.isEmpty()) {
            return message != null ? message : "An error occurred";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : errors.entrySet()) {
            for (String error : entry.getValue()) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(error);
            }
        }
        return sb.toString();
    }
}
