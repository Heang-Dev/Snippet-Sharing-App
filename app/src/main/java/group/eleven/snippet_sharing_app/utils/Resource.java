package group.eleven.snippet_sharing_app.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A generic wrapper class for data with loading/error states.
 * Used with LiveData to communicate loading states and errors.
 *
 * @param <T> The type of data being wrapped
 */
public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    /**
     * Creates a success resource with data
     */
    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    /**
     * Creates an error resource with message
     */
    public static <T> Resource<T> error(@NonNull String message) {
        return new Resource<>(Status.ERROR, null, message);
    }

    /**
     * Creates an error resource with message and existing data
     */
    public static <T> Resource<T> error(@NonNull String message, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, message);
    }

    /**
     * Creates a loading resource
     */
    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    /**
     * Creates a loading resource with existing data
     */
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    /**
     * Checks if the resource is in success state
     */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    /**
     * Checks if the resource is in loading state
     */
    public boolean isLoading() {
        return status == Status.LOADING;
    }

    /**
     * Checks if the resource is in error state
     */
    public boolean isError() {
        return status == Status.ERROR;
    }

    /**
     * Status enum for Resource states
     */
    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }
}
