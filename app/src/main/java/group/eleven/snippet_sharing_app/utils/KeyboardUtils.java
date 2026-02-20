package group.eleven.snippet_sharing_app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Utility class for handling keyboard operations across the app.
 * Provides functionality to:
 * - Hide keyboard when tapping outside input fields
 * - Show/hide keyboard programmatically
 * - Setup automatic keyboard dismissal for activities
 */
public class KeyboardUtils {

    /**
     * Hides the soft keyboard.
     *
     * @param activity The current activity
     */
    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = activity.getWindow().getDecorView();
        }

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Clear focus from any EditText
        if (view != null) {
            view.clearFocus();
        }
    }

    /**
     * Hides the soft keyboard using a view reference.
     *
     * @param view Any view in the activity
     */
    public static void hideKeyboard(View view) {
        if (view == null) return;

        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard for an EditText.
     *
     * @param editText The EditText to focus and show keyboard for
     */
    public static void showKeyboard(EditText editText) {
        if (editText == null) return;

        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Sets up automatic keyboard dismissal when user taps outside of EditText fields.
     * Call this method in Activity's onCreate() or in dispatchTouchEvent().
     *
     * @param activity The activity to setup
     * @param rootView The root view of the activity's layout
     */
    public static void setupKeyboardDismissOnOutsideTouch(Activity activity, View rootView) {
        if (activity == null || rootView == null) return;

        // Make the root view focusable so it can receive focus when tapping outside
        rootView.setFocusable(true);
        rootView.setFocusableInTouchMode(true);
        rootView.setClickable(true);

        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View currentFocus = activity.getCurrentFocus();
                if (currentFocus instanceof EditText) {
                    Rect outRect = new Rect();
                    currentFocus.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        hideKeyboard(activity);
                        currentFocus.clearFocus();
                    }
                }
            }
            return false; // Allow the event to continue propagating
        });
    }

    /**
     * Call this method from Activity's dispatchTouchEvent() for automatic keyboard dismissal.
     * This is the preferred method as it works globally without modifying individual views.
     *
     * @param activity The current activity
     * @param event The motion event from dispatchTouchEvent
     * @return true if the touch was outside an EditText and keyboard was hidden
     */
    public static boolean handleTouchOutsideEditText(Activity activity, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus instanceof EditText) {
                Rect outRect = new Rect();
                currentFocus.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    hideKeyboard(activity);
                    currentFocus.clearFocus();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Recursively sets up touch listeners on all non-EditText views to dismiss keyboard.
     * This is a more comprehensive approach that handles nested views.
     *
     * @param activity The activity
     * @param view The view to process (usually the root view)
     */
    public static void setupRecursiveKeyboardDismiss(final Activity activity, View view) {
        // Set up touch listener for non-text box views to hide keyboard
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard(activity);
                }
                return false;
            });
        }

        // If a layout container, iterate over children and seed recursion
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupRecursiveKeyboardDismiss(activity, innerView);
            }
        }
    }

    /**
     * Check if the keyboard is currently visible.
     *
     * @param rootView The root view of the activity
     * @return true if keyboard is visible
     */
    public static boolean isKeyboardVisible(View rootView) {
        if (rootView == null) return false;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        int screenHeight = rootView.getRootView().getHeight();
        int keypadHeight = screenHeight - r.bottom;

        // If keypadHeight is more than 15% of the screen, assume it's the keyboard
        return keypadHeight > screenHeight * 0.15;
    }
}
