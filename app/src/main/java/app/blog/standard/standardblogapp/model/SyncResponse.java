package app.blog.standard.standardblogapp.model;

/**
 * @author victor
 */
public class SyncResponse {

    /** ERROR TYPES **/
    public static final int NO_ERROR = -1;
    public static final int ERROR_DOWNLOADING = 0;
    public static final int ERROR_PARSING_DATA = 1;
    public static final int ERROR_ON_DATABASE = 2;

    private boolean success;
    private int newPosts, errorType;
    private String message;

    public SyncResponse(int errorType, String message, int newPosts, boolean success) {
        this.errorType = errorType;
        this.message = message;
        this.newPosts = newPosts;
        this.success = success;
    }

    public SyncResponse() {
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNewPosts() {
        return newPosts;
    }

    public void setNewPosts(int newPosts) {
        this.newPosts = newPosts;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}