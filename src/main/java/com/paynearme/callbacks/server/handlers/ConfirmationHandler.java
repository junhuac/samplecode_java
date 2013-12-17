package com.paynearme.callbacks.server.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Request handler for Confirmation callbacks. This is responsible for ... TODO: docs
 */
public interface ConfirmationHandler {
    public void handleConfirmationRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
