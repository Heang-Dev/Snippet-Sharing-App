package group.eleven.snippet_sharing_app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

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
        if (view instanceof EditText) {
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
     * Call this method in Activity's onCreate() after setContentView().
     * Works with ScrollView, NestedScrollView, and regular layouts.
     *
     * @param activity The activity to setup
     * @param rootView The root view of the activity's layout
     */
    public static void setupKeyboardDismissOnOutsideTouch(Activity activity, View rootView) {
        if (activity == null || rootView == null) return;

        // Find the actual content view (could be ScrollView, NestedScrollView, or direct content)
        View targetView = rootView;

        // If root is a ScrollView or NestedScrollView, we need to handle it specially
        if (rootView instanceof ScrollView || rootView instanceof NestedScrollView) {
            targetView = rootView;
        }

        // Make the view focusable so it can receive focus when tapping outside
        targetView.setFocusable(true);
        targetView.setFocusableInTouchMode(true);

        final View finalTargetView = targetView;
        targetView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View currentFocus = activity.getCurrentFocus();
                if (currentFocus instanceof EditText) {
                    Rect outRect = new Rect();
                    currentFocus.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        hideKeyboard(activity);
                        currentFocus.clearFocus();
                        // Request focus on root to remove focus from EditText
                        finalTargetView.requestFocus();
                    }
                }
            }
            return false; // Allow the event to continue propagating
        });

        // Also setup on child views if it's a ViewGroup
        if (rootView instanceof ViewGroup) {
            setupTouchListenerOnNonEditTextViews(activity, (ViewGroup) rootView);
        }
    }

    /**
     * Recursively sets up touch listeners on non-EditText views within a ViewGroup.
     */
    private static void setupTouchListenerOnNonEditTextViews(Activity activity, ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            // Skip EditText views
            if (child instanceof EditText) {
                continue;
            }

            // For ViewGroups that are not scrollable containers, recurse
            if (child instanceof ViewGroup &&
                !(child instanceof ScrollView) &&
                !(child instanceof NestedScrollView)) {
                setupTouchListenerOnNonEditTextViews(activity, (ViewGroup) child);
            }
        }
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

    /**
     * Sets up automatic scrolling to focused EditText when keyboard opens.
     * Call this in Activity's onCreate() after setContentView().
     * Works with ScrollView, NestedScrollView, and CoordinatorLayout with NestedScrollView.
     *
     * @param activity The activity
     * @param rootView The root view of the activity's layout
     */
    public static void setupScrollToFocusedInput(Activity activity, View rootView) {
        if (activity == null || rootView == null) return;

        // Find the scrollable container
        final View scrollContainer = findScrollContainer(rootView);
        if (scrollContainer == null) return;

        // Track keyboard state to avoid duplicate scrolls
        final boolean[] wasKeyboardOpen = {false};

        // Add keyboard visibility listener
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            boolean isKeyboardOpen = keypadHeight > screenHeight * 0.15;

            // Only scroll when keyboard just opened
            if (isKeyboardOpen && !wasKeyboardOpen[0]) {
                wasKeyboardOpen[0] = true;
                View focusedView = activity.getCurrentFocus();
                if (focusedView instanceof EditText) {
                    // Delay scroll to ensure layout is complete after resize
                    final View finalFocusedView = focusedView;
                    scrollContainer.postDelayed(() -> {
                        scrollToViewWithPadding(scrollContainer, finalFocusedView, 150);
                    }, 100);
                }
            } else if (!isKeyboardOpen) {
                wasKeyboardOpen[0] = false;
            }
        });

        // Also scroll when focus changes while keyboard is open
        setupFocusChangeListener(activity, rootView, scrollContainer);
    }

    /**
     * Sets up focus change listener to scroll when user taps different EditText.
     */
    private static void setupFocusChangeListener(Activity activity, View rootView, View scrollContainer) {
        rootView.getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
            if (newFocus instanceof EditText && isKeyboardVisible(rootView)) {
                scrollContainer.postDelayed(() -> {
                    scrollToViewWithPadding(scrollContainer, newFocus, 150);
                }, 100);
            }
        });
    }

    /**
     * Finds the scrollable container in the view hierarchy.
     */
    private static View findScrollContainer(View view) {
        if (view instanceof ScrollView || view instanceof NestedScrollView) {
            return view;
        }

        if (view instanceof CoordinatorLayout) {
            CoordinatorLayout coordinator = (CoordinatorLayout) view;
            for (int i = 0; i < coordinator.getChildCount(); i++) {
                View child = coordinator.getChildAt(i);
                if (child instanceof NestedScrollView) {
                    return child;
                }
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View found = findScrollContainer(viewGroup.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    /**
     * Scrolls to make the target view visible within the scroll container with extra padding.
     */
    private static void scrollToViewWithPadding(View scrollContainer, View targetView, int extraPadding) {
        if (scrollContainer == null || targetView == null) return;

        // Get target view's position relative to the scroll container
        Rect targetRect = new Rect();
        targetView.getDrawingRect(targetRect);

        // Offset the rect to scroll container coordinates
        try {
            ((ViewGroup) scrollContainer).offsetDescendantRectToMyCoords(targetView, targetRect);
        } catch (Exception e) {
            // Fallback to screen-based calculation
            scrollToViewFallback(scrollContainer, targetView, extraPadding);
            return;
        }

        // Calculate if we need to scroll
        int scrollY = 0;
        if (scrollContainer instanceof ScrollView) {
            scrollY = ((ScrollView) scrollContainer).getScrollY();
        } else if (scrollContainer instanceof NestedScrollView) {
            scrollY = ((NestedScrollView) scrollContainer).getScrollY();
        }

        int containerHeight = scrollContainer.getHeight();
        int targetBottom = targetRect.bottom + extraPadding;
        int visibleBottom = scrollY + containerHeight;

        // If target is below visible area, scroll to show it
        if (targetBottom > visibleBottom) {
            int scrollAmount = targetBottom - visibleBottom;
            if (scrollContainer instanceof ScrollView) {
                ((ScrollView) scrollContainer).smoothScrollBy(0, scrollAmount);
            } else if (scrollContainer instanceof NestedScrollView) {
                ((NestedScrollView) scrollContainer).smoothScrollBy(0, scrollAmount);
            }
        }
        // If target is above visible area, scroll up to show it
        else if (targetRect.top < scrollY) {
            int scrollAmount = targetRect.top - scrollY - extraPadding;
            if (scrollContainer instanceof ScrollView) {
                ((ScrollView) scrollContainer).smoothScrollBy(0, scrollAmount);
            } else if (scrollContainer instanceof NestedScrollView) {
                ((NestedScrollView) scrollContainer).smoothScrollBy(0, scrollAmount);
            }
        }
    }

    /**
     * Fallback scroll method using screen coordinates.
     */
    private static void scrollToViewFallback(View scrollContainer, View targetView, int extraPadding) {
        int[] targetLocation = new int[2];
        int[] scrollLocation = new int[2];

        targetView.getLocationOnScreen(targetLocation);
        scrollContainer.getLocationOnScreen(scrollLocation);

        int targetBottom = targetLocation[1] + targetView.getHeight() + extraPadding;
        int scrollBottom = scrollLocation[1] + scrollContainer.getHeight();

        if (targetBottom > scrollBottom) {
            int scrollAmount = targetBottom - scrollBottom;
            if (scrollContainer instanceof ScrollView) {
                ((ScrollView) scrollContainer).smoothScrollBy(0, scrollAmount);
            } else if (scrollContainer instanceof NestedScrollView) {
                ((NestedScrollView) scrollContainer).smoothScrollBy(0, scrollAmount);
            }
        }
    }
}
