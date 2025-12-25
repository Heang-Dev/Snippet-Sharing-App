package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for forgot password endpoint
 */
public class ForgotPasswordResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ForgotPasswordData data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ForgotPasswordData getData() {
        return data;
    }

    public void setData(ForgotPasswordData data) {
        this.data = data;
    }

    public String getToken() {
        return data != null ? data.getToken() : null;
    }

    public int getExpiresIn() {
        return data != null ? data.getExpiresIn() : 0;
    }
}
