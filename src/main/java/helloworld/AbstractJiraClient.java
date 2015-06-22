package helloworld;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbstractJiraClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractJiraClient.class);

    protected <T> List<T> parseResponse(String response, Class<T> clazz) throws ResponseException {
        JsonElement parsedResponse = new JsonParser().parse(response);
        if (parsedResponse != null && parsedResponse.isJsonArray()) {
            JsonArray jsonArray = parsedResponse.getAsJsonArray();
            if (jsonArray.size() > 0) {
                return unpackToObjects(response, jsonArray, clazz);
            }
        }
        return Collections.emptyList();
    }

    protected <T> List<T> unpackToObjects(String response, JsonArray jsonArray, Class<T> clazz) throws ResponseException {
        List<T> elements = new ArrayList<T>(jsonArray.size());
        ObjectMapper mapper = new ObjectMapper();
        try {
            for (JsonElement jsonElement : jsonArray) {
                T element = mapper.readValue(jsonElement.toString(), clazz);
                elements.add(element);
            }
        } catch (IOException e) {
            throw new ResponseException("There is a problem processing the response from JIRA: unrecognisable response: " + response, e);
        }
        return elements;
    }

    protected ApplicationLinkRequest createRequest(ApplicationLink appLink, Request.MethodType methodType, String baseRestUrl) throws CredentialsRequiredException {
        String url = appLink.getRpcUrl() + baseRestUrl;
        log.debug("create request: {} {}", methodType, url);
        ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory();
        ApplicationLinkRequest request;
        try {
            request = requestFactory.createRequest(methodType, url);
        } catch (CredentialsRequiredException e) {
            requestFactory = appLink.createAuthenticatedRequestFactory(Anonymous.class);
            request = requestFactory.createRequest(methodType, url);
        }
        return request;
    }

    protected String executeRequest(ApplicationLinkRequest appLinkRequest) throws ResponseException {
        appLinkRequest.toString();
        return appLinkRequest.executeAndReturn(new ReturningResponseHandler<Response, String>() {
            @Override
            public String handle(Response response) throws ResponseException {
                if (response.isSuccessful()) {
                    return response.getResponseBodyAsString();
                }
                throw new RequestException(response.getStatusCode(), response.getStatusText(), response.getResponseBodyAsString());
            }
        });
    }

}
