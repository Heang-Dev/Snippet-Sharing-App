package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for OTP verification endpoint
 */
public class OtpVerifyResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private OtpVerifyData data;

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

    public OtpVerifyData getData() {
        return data;
    }

    public void setData(OtpVerifyData data) {
        this.data = data;
    }

    public String getResetToken() {
        return data != null ? data.getResetToken() : null;
    }

    public String getEmail() {
        return data != null ? data.getEmail() : null;
    }
}
