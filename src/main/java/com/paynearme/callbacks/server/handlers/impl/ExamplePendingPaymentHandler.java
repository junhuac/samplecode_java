package com.paynearme.callbacks.server.handlers.impl;

import com.paynearme.callbacks.response.PendingPaymentResponseBuilder;
import com.paynearme.callbacks.enums.Version;
import com.paynearme.callbacks.server.handlers.PendingPaymentHandler;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Example of a {@link PendingPaymentHandler}. This will always return the pnm_order_identifier that was sent to it.
 */
public class ExamplePendingPaymentHandler implements PendingPaymentHandler {
    @Override
    public void handlePendingPaymentRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter writer = response.getWriter();
        String identifier = request.getParameter("pnm_order_identifier");
        Version apiVersion = Version.getVersionFromString(request.getParameter("version"));

        response.setStatus(HttpServletResponse.SC_OK);
        PendingPaymentResponseBuilder pending = new PendingPaymentResponseBuilder(apiVersion);
        pending.setPnmOrderIdentifier(identifier);
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        out.output(pending.build(), writer);
    }
}
