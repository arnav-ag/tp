package seedu.address.wrapper;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.wrapper.UserReposRoute.getUserReposRoute;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import kong.unirest.UnirestInstance;
import seedu.address.wrapper.exceptions.RepoNotFoundException;

/**
 * Class representing a wrapper over the requests and routes needed to get user repo information from GitHub
 */
public class UserReposWrapper {
    //@@author arnav-ag

    private final static String ID_KEY = "id";
    private final static String NAME_KEY = "name";
    private final static String URL_KEY = "html_url";
    private final static String FORKS_KEY = "forks_count";
    private final static String UPDATED_KEY = "updated_at";
    private final UserReposRoute.UserReposRequest getUserReposRequest;
    private JSONArray reposJson;

    /**
     * @param username Username of GitHub user to initialise class
     * @param unirest  Unirest instance used for all further requests
     */
    public UserReposWrapper(String username, UnirestInstance unirest) {
        requireAllNonNull(username);

        UserReposRoute getUserInfoGetInfoRoute = getUserReposRoute(username);
        getUserReposRequest = getUserInfoGetInfoRoute.createRequest(unirest);
        updateReposJson();
    }

    private void updateReposJson() {
        this.reposJson = getUserReposRequest.getJson();
    }

    public ArrayList<Integer> getIDs() {
        ArrayList<Integer> result = new ArrayList<>();
        JSONObject obj;
        for (int i = 0; i < this.reposJson.length(); i++) {
            obj = (JSONObject) this.reposJson.get(i);
            result.add(obj.getInt(ID_KEY));
        }

        return result;
    }

    public String getRepoName(int id) {
        JSONObject obj;
        for (int i = 0; i < this.reposJson.length(); i++) {
            obj = (JSONObject) this.reposJson.get(i);
            if (obj.getInt("id") == id) {
                return obj.getString(NAME_KEY);
            }
        }

        throw new RepoNotFoundException("Provided ID does not correspond to a repository owned by this user!");
    }

    public String getRepoUrl(int id) {
        JSONObject obj;
        for (int i = 0; i < this.reposJson.length(); i++) {
            obj = (JSONObject) this.reposJson.get(i);
            if (obj.getInt("id") == id) {
                return obj.getString(URL_KEY);
            }
        }

        throw new RepoNotFoundException("Provided ID does not correspond to a repository owned by this user!");
    }

    public LocalDateTime getLastUpdated(int id) {
        JSONObject obj;
        for (int i = 0; i < this.reposJson.length(); i++) {
            obj = (JSONObject) this.reposJson.get(i);
            if (obj.getInt("id") == id) {
                return LocalDateTime.parse(obj.getString(UPDATED_KEY), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
        }

        throw new RepoNotFoundException("Provided ID does not correspond to a repository owned by this user!");
    }

    public int getRepoForkCount(int id) {
        JSONObject obj;
        for (int i = 0; i < this.reposJson.length(); i++) {
            obj = (JSONObject) this.reposJson.get(i);
            if (obj.getInt("id") == id) {
                return obj.getInt(FORKS_KEY);
            }
        }

        throw new RepoNotFoundException("Provided ID does not correspond to a repository owned by this user!");
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof UserReposWrapper)
                && getUserReposRequest.equals(((UserReposWrapper) other).getUserReposRequest)
                && reposJson.equals(((UserReposWrapper) other).reposJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserReposRequest, reposJson);
    }
}
