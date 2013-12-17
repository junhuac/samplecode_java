package com.paynearme.callbacks.server.handlers.impl;

import com.paynearme.callbacks.response.AuthorizationResponseBuilder;
import com.paynearme.callbacks.enums.Version;
import com.paynearme.callbacks.server.Constants;
import com.paynearme.callbacks.server.InvalidSignatureException;
import com.paynearme.callbacks.server.SignatureUtils;
import com.paynearme.callbacks.server.handlers.AuthorizationHandler;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ryan on 11/21/13.
 */
public class ExampleAuthorizationHandler implements AuthorizationHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.trace("Begin handleAuthorizationRequest");

        logger.debug("Verifying Signature...");
        boolean validSignature = true;
        try {
            SignatureUtils.validateSignature(request, Constants.SECRET);
        } catch (InvalidSignatureException e) {
            logger.error("Invalid signature, declining authorization request.", e);
            validSignature = false;
        }

        Version apiVersion = Version.getVersionFromString(request.getParameter("version"));

        String orderIdentifier = request.getParameter("pnm_order_identifier");

        AuthorizationResponseBuilder auth = new AuthorizationResponseBuilder(apiVersion);
        auth.setOrderIdentifier(orderIdentifier);

        /* This is where you would verify the information sent with the
           request, validate it within your database, and then return a
           response
         */
        boolean accept = false;
        if (orderIdentifier != null && orderIdentifier.equals("TEST-123")) {
            accept = true;
            logger.debug("Example authorization of TEST-123 will be ACCEPTED");
        }

        if (accept && validSignature) {
            auth.setAcceptPayment(true);
            auth.setReceipt("Thank you for your order!");
        } else {
            auth.setAcceptPayment(false);
            auth.setReceipt("Declined");
            auth.setMemo("Invalid payment: " + orderIdentifier);
        }
        auth.setMemo(new Date().toString());

        XMLOutputter out = new XMLOutputter();
        out.output(auth.build(), response.getOutputStream());

        logger.trace("End handleAuthorizationRequest");
    }
}
