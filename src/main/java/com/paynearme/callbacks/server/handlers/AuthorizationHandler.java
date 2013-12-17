package com.paynearme.callbacks.server.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An {@link AuthorizationHandler} is responsible for handling an authorization
 * request. It is expected to do the following:
 *   1. Check if the request is a valid
 *   2. Generate the appropriate response
 *
 * AuthorizationHandler's are responsible for handling their own signature validation, as we want to return a declined
 * message when the signature is invalid.
 *
 * @see com.paynearme.callbacks.server.handlers.impl.ExampleAuthorizationHandler for a sample and starting point.
 */
public interface AuthorizationHandler {
    public void handleAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
