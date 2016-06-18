package app.blog.standard.standardblogapp.model;

/**
 * @author victor
 */
public class SyncResponse {

    /** ERROR TYPES **/
    public static final int NO_ERROR = -1;
    public static final int ERROR_DOWNLOADING = 1;
    public static final int ERROR_PARSING_DATA = 2;
    public static final int ERROR_ON_DATABASE = 3;

    private int newPosts, errorType;

    public SyncResponse(int errorType, int newPosts) {
        this.errorType = errorType;
        this.newPosts = newPosts;
    }

    public SyncResponse() {
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public int getNewPosts() {
        return newPosts;
    }

    public void setNewPosts(int newPosts) {
        this.newPosts = newPosts;
    }

    public boolean isSuccess() {
        return errorType == NO_ERROR;
    }

    public boolean hasNewPosts() { return newPosts > 0; }
}
