package com.paynearme.callbacks.server.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Pending Payment handler.
 *
 * This is for the /pending call. In older versions of the PayNearMe API this
 * call was used for pending payments. In 2.0 this call has become deprecated.
 *
 * @deprecated
 */
public interface PendingPaymentHandler {
    public void handlePendingPaymentRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
