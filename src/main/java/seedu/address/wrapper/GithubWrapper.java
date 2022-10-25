package seedu.address.wrapper;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import kong.unirest.Config;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponse;
import kong.unirest.Interceptor;
import kong.unirest.UnirestInstance;
import seedu.address.model.person.github.User;
import seedu.address.storage.Storage;
import seedu.address.wrapper.exceptions.NetworkConnectionException;
import seedu.address.wrapper.exceptions.RepoNotFoundException;
import seedu.address.wrapper.exceptions.UserInvalidException;

public class GithubWrapper {
    //@@author arnav-ag
    private final static String BASE_CHECK_USER_URL = "https://api.github.com/users/";
    private final static String BASE_GITHUB_URL = "https://www.github.com/";
    final private UnirestInstance unirest;
    final private Storage storage;

    public GithubWrapper(Storage storage) {

        unirest = getDefaultUnirestInstance();
        this.storage = storage;
    }

    User getUser(String username) throws UserInvalidException, NetworkConnectionException {
        requireAllNonNull(username);
        checkUserExists(username, unirest);
        UserInfoWrapper userInfoWrapper = new UserInfoWrapper(username, unirest, storage);
        UserReposWrapper userReposWrapper = new UserReposWrapper(username, unirest);

        return new User(username, userInfoWrapper, userReposWrapper);
    }

    public static UnirestInstance getDefaultUnirestInstance() {
        Config config = new Config()
            .interceptor(new Interceptor() {
                @Override
                public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
                    if (response.getStatus() == 404) {
                        throw new UserInvalidException(
                            "User does not exist. Please provide an existing GitHub username.");
                    }
                }

                @Override
                public HttpResponse<?> onFail(Exception e, HttpRequestSummary request, Config config)
                    throws RepoNotFoundException {
                    throw new NetworkConnectionException("Error while getting request, unable to get results.");
                }
            });
        return new UnirestInstance(config);
    }

    private void checkUserExists(String username, UnirestInstance unirest)
        throws UserInvalidException, NetworkConnectionException {
        unirest.get(BASE_CHECK_USER_URL + username).asEmpty();
    }
}