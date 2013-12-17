package com.paynearme.callbacks.server.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Request handler for Confirmation callbacks. This is responsible for handling
 * the /confirm callback. The server should always return the pnm_order_identifier
 * to acknowledge the payment. PayNearMe will retry this call if a response is not
 * appropriately served, so you MUST respond, and MUST track confirmed orders.
 *
 * Once you get the pnm_order_identifier you need to record it somewhere and
 * prevent it from being double-posted.
 */
public interface ConfirmationHandler {
    public void handleConfirmationRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
