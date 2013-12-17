package com.paynearme.callbacks.server.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ryan on 12/13/13.
 */
public interface PendingPaymentHandler {
    public void handlePendingPaymentRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
