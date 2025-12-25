package group.eleven.snippet_sharing_app.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Form validation utility for real-time form validation
 */
public class FormValidator {

    // Validation patterns
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,30}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{8,}$");
    private static final Pattern OTP_PATTERN = Pattern.compile("^[0-9]{6}$");

    private final List<FieldValidator> fieldValidators = new ArrayList<>();
    private Button submitButton;
    private OnValidationChangeListener validationChangeListener;

    public interface OnValidationChangeListener {
        void onValidationChanged(boolean isValid);
    }

    /**
     * Set the submit button to enable/disable based on form validity
     */
    public FormValidator setSubmitButton(Button button) {
        this.submitButton = button;
        updateSubmitButtonState();
        return this;
    }

    /**
     * Set validation change listener
     */
    public FormValidator setOnValidationChangeListener(OnValidationChangeListener listener) {
        this.validationChangeListener = listener;
        return this;
    }

    /**
     * Add a required field
     */
    public FormValidator addRequiredField(TextInputLayout layout, TextInputEditText editText, String errorMessage) {
        FieldValidator validator = new FieldValidator(layout, editText, text -> {
            if (TextUtils.isEmpty(text)) {
                return errorMessage;
            }
            return null;
        });
        fieldValidators.add(validator);
        setupTextWatcher(editText);
        return this;
    }

    /**
     * Add email field validation
     */
    public FormValidator addEmailField(TextInputLayout layout, TextInputEditText editText,
                                        String requiredError, String invalidError) {
        FieldValidator validator = new FieldValidator(layout, editText, text -> {
            if (TextUtils.isEmpty(text)) {
                return requiredError;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                return invalidError;
            }
            return null;
        });
        fieldValidators.add(validator);
        setupTextWatcher(editText);
        return this;
    }

    /**
     * Add username field validation
     */
    public FormValidator addUsernameField(TextInputLayout layout, TextInputEditText editText,
                                           String requiredError, String invalidError) {
        FieldValidator validator = new FieldValidator(layout, editText, text -> {
            if (TextUtils.isEmpty(text)) {
                return requiredError;
            }
            if (!USERNAME_PATTERN.matcher(text).matches()) {
                return invalidError;
            }
            return null;
        });
        fieldValidators.add(validator);
        setupTextWatcher(editText);
        return this;
    }

    /**
     * Add password field validation
     */
    public FormValidator addPasswordField(TextInputLayout layout, TextInputEditText editText,
                                           String requiredError, String tooShortError) {
        FieldValidator validator = new FieldValidator(layout, editText, text -> {
            if (TextUtils.isEmpty(text)) {
                return requiredError;
            }
            if (text.length() < 8) {
                return tooShortError;
            }
            return null;
        });
        fieldValidators.add(validator);
        setupTextWatcher(editText);
        return this;
    }

    /**
     * Add confirm password field validation
     */
    public FormValidator addConfirmPasswordField(TextInputLayout layout, TextInputEditText editText,
                                                  TextInputEditText passwordEditText,
                                                  String requiredError, String mismatchError) {
        FieldValidator validator = new FieldValidator(layout, editText, text -> {
            if (TextUtils.isEmpty(text)) {
                return requiredError;
            }
            String password = passwordEditText.getText() != null ?
                    passwordEditText.getText().toString() : "";
            if (!text.equals(password)) {
                return mismatchError;
            }
            return null;
        });
        fieldValidators.add(validator);
        setupTextWatcher(editText);
        // Also watch password field for changes
        setupTextWatcher(passwordEditText);
        return this;
    }

    /**
     * Add OTP field validation
     */
    public FormValidator addOtpField(TextInputLayout layout, TextInputEditText editText,
                                      String requiredError, String invalidError) {
        FieldValidator validator = new FieldValidator(layout, editText, text -> {
            if (TextUtils.isEmpty(text)) {
                return requiredError;
            }
            if (!OTP_PATTERN.matcher(text).matches()) {
                return invalidError;
            }
            return null;
        });
        fieldValidators.add(validator);
        setupTextWatcher(editText);
        return this;
    }

    /**
     * Add login field (email or username)
     */
    public FormValidator addLoginField(TextInputLayout layout, TextInputEditText editText,
                                        String requiredError) {
        FieldValidator validator = new FieldValidator(layout, editText, text -> {
            if (TextUtils.isEmpty(text)) {
                return requiredError;
            }
            return null;
        });
        fieldValidators.add(validator);
        setupTextWatcher(editText);
        return this;
    }

    /**
     * Add custom validation
     */
    public FormValidator addCustomField(TextInputLayout layout, TextInputEditText editText,
                                         ValidationRule rule) {
        FieldValidator validator = new FieldValidator(layout, editText, rule);
        fieldValidators.add(validator);
        setupTextWatcher(editText);
        return this;
    }

    private void setupTextWatcher(TextInputEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Clear error when user types
                for (FieldValidator fv : fieldValidators) {
                    if (fv.editText == editText) {
                        fv.layout.setError(null);
                        fv.layout.setErrorEnabled(false);
                        break;
                    }
                }
                updateSubmitButtonState();
            }
        });
    }

    /**
     * Check if all fields are valid (without showing errors)
     */
    public boolean isFormValid() {
        for (FieldValidator validator : fieldValidators) {
            String text = validator.editText.getText() != null ?
                    validator.editText.getText().toString().trim() : "";
            String error = validator.rule.validate(text);
            if (error != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate all fields and show errors
     */
    public boolean validateAll() {
        boolean isValid = true;
        for (FieldValidator validator : fieldValidators) {
            String text = validator.editText.getText() != null ?
                    validator.editText.getText().toString().trim() : "";
            String error = validator.rule.validate(text);
            if (error != null) {
                validator.layout.setError(error);
                validator.layout.setErrorEnabled(true);
                isValid = false;
            } else {
                validator.layout.setError(null);
                validator.layout.setErrorEnabled(false);
            }
        }
        return isValid;
    }

    /**
     * Update submit button state based on form validity
     */
    private void updateSubmitButtonState() {
        boolean isValid = isFormValid();
        if (submitButton != null) {
            submitButton.setEnabled(isValid);
            submitButton.setAlpha(isValid ? 1.0f : 0.6f);
        }
        if (validationChangeListener != null) {
            validationChangeListener.onValidationChanged(isValid);
        }
    }

    /**
     * Clear all errors
     */
    public void clearErrors() {
        for (FieldValidator validator : fieldValidators) {
            validator.layout.setError(null);
            validator.layout.setErrorEnabled(false);
        }
    }

    /**
     * Validation rule interface
     */
    public interface ValidationRule {
        /**
         * Validate the text
         * @param text The text to validate
         * @return Error message if invalid, null if valid
         */
        String validate(String text);
    }

    /**
     * Field validator holder
     */
    private static class FieldValidator {
        final TextInputLayout layout;
        final TextInputEditText editText;
        final ValidationRule rule;

        FieldValidator(TextInputLayout layout, TextInputEditText editText, ValidationRule rule) {
            this.layout = layout;
            this.editText = editText;
            this.rule = rule;
        }
    }
}
