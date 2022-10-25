package seedu.address.wrapper;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import kong.unirest.Config;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponse;
import kong.unirest.Interceptor;
import kong.unirest.UnirestInstance;
import seedu.address.wrapper.exceptions.NetworkConnectionException;
import seedu.address.wrapper.exceptions.UserInvalidException;

public class GithubWrapper {

    private final static String BASE_CHECK_USER_URL = "https://api.github.com/users/";
    final private UserInfoWrapper userInfoWrapper;
    final private UserReposWrapper userReposWrapper;

    public GithubWrapper(String username) throws UserInvalidException, NetworkConnectionException {
        requireAllNonNull(username);
        UnirestInstance unirest = getDefaultUnirestInstance();
        checkUserExists(username, unirest);
        userInfoWrapper = new UserInfoWrapper(username, unirest);
        userReposWrapper = new UserReposWrapper(username, unirest);
    }

    private UnirestInstance getDefaultUnirestInstance() {
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
                    throws NetworkConnectionException {
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
