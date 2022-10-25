package seedu.address.wrapper;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import seedu.address.storage.Storage;
import seedu.address.wrapper.exceptions.ResponseParseException;

public final class UserInfoRoute {
    //@@author arnav-ag

    public static final String BASE_GITHUB_URL = "https://api.github.com";
    private static final String SERVER_DOWN_STATUS_MESSAGE = "Unable to get information from API.";

    private final static String GET_USER_BASE_PATH = "/users/";
    private final String path;
    private final Storage storage;
    private final String username;

    private UserInfoRoute(String path, String username, Storage storage) {
        requireAllNonNull(path, username, storage);
        this.path = path;
        this.storage = storage;
        this.username = username;
    }

    public static UserInfoRoute getUserInfoRoute(String username, Storage storage) {
        requireAllNonNull(username);
        return new UserInfoRoute(GET_USER_BASE_PATH + username, username, storage);
    }

    public UserInfoRequest createRequest(UnirestInstance unirest) {
        requireAllNonNull(unirest);

        return new UserInfoRequest(unirest, BASE_GITHUB_URL + this.path, storage, username);
    }

    public String getPath() {
        return this.path;
    }

    public static class UserInfoRequest {
        private final Storage storage;

        private final UnirestInstance unirest;
        private final String url;

        private final String username;

        UserInfoRequest(UnirestInstance unirest, String url, Storage storage, String username) {
            requireAllNonNull(unirest, url, storage, username);
            this.unirest = unirest;
            this.url = url;
            this.storage = storage;
            this.username = username;
        }

        public JSONObject getJSON() {
            var response = this.unirest.get(this.url).asString().getBody();

            try {
                return new JSONObject(response);
            } catch (JSONException e) {
                throw new ResponseParseException("Unable to parse API result.");
            }
        }

        public void downloadAvatarImage(String fileUrl) {
            Unirest.get(fileUrl).thenConsume(rawResponse -> {
                try {
                    BufferedImage image = ImageIO.read(rawResponse.getContent());
                    storage.saveImage(image, username + ".png");
                } catch (IOException e) {
                    throw new ResponseParseException("Error saving user avatar.");
                }
            });

        }

    }

}