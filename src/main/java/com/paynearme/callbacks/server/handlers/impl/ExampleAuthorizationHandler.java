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
import java.util.Map;

/**
 * Created by ryan on 11/21/13.
 */
public class ExampleAuthorizationHandler implements AuthorizationHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.trace("Begin handleAuthorizationRequest");
        logger.info("Handling /authorize with ExampleAuthorizationHandler");

        logger.debug("Verifying Signature...");
        boolean validSignature = true;
        try {
            SignatureUtils.validateSignature(request, Constants.SECRET);
        } catch (InvalidSignatureException e) {
            logger.error("Invalid signature, declining authorization request.", e);
            validSignature = false;
        }

        // If the url contains the parameter test=true (part of the signed params too!) then we flag this.
        // Do not handle test=true requests as real requests.
        boolean isTest = request.getParameter("test") != null && Boolean.parseBoolean(request.getParameter("test"));
        if (isTest) {
            logger.info("This authorize request is a TEST!");
        }

        // Special behavior
        Map<String, String[]> params = request.getParameterMap();
        try {
            String special = params.get("site_order_annotation")[0];
            if (special.startsWith("confirm_delay_")) {
                int delay = Integer.parseInt(special.substring(special.lastIndexOf('_') + 1)) * 1000;
                logger.info("Delaying response by {} seconds", delay);
                Thread.sleep(delay);
            } else if (special.equals("confirm_bad_xml")) {
                logger.info("Responding with bad/broken xml");
                response.getWriter().print("<result");
                response.getWriter().flush();
                logger.trace("End handleConfirmationRequest (early: bad xml)");
                return;
            } else if (special.equals("confirm_blank")) {
                logger.info("Responding with a blank/empty response");
                logger.trace("End handleConfirmationRequest (early: blank response)");
                return;
            } else if (special.equals("confirm_redirect")) {
                logger.info("Redirecting to /");
                response.sendRedirect("/");
                logger.trace("End handleConfirmationRequest (early: redirect)");
                return;
            }
        } catch (NullPointerException | InterruptedException e) {
            // Can't handle the annotation, or our delay was interrupted.
            logger.debug("NPE in the parameter site_order_annotation - not doing any special behavior");
        }

        Version apiVersion = Version.getVersionFromString(request.getParameter("version"));
        String pnmOrderIdentifier = request.getParameter("pnm_order_identifier");
        String siteOrderIdentifier = request.getParameter("site_order_identifier");


        /* This is where you verify the information sent with the
           request, validate it within your system, and then return a
           response. Here we just accept payments with order identifiers of
           "TEST-123" if the request is test mode.
         */

        AuthorizationResponseBuilder auth = new AuthorizationResponseBuilder(apiVersion);

        auth.setOrderIdentifier(pnmOrderIdentifier);

        boolean accept = false;
        if (siteOrderIdentifier != null && siteOrderIdentifier.startsWith("TEST")) {
            accept = true;
            logger.info("Example authorization {} will be ACCEPTED", siteOrderIdentifier);
        } else {
            logger.info("Example authorization {} will be DECLINED", siteOrderIdentifier);
        }

        if (accept && validSignature) {
            auth.setAcceptPayment(true);
            /* You can set custom receipt text here (if you want) - if you
               don't want custom text, you can omit this
            */
            auth.setReceipt("Thank you for your order!");
            auth.setMemo(new Date().toString());
        } else {
            auth.setAcceptPayment(false);
            auth.setReceipt("Declined");
            auth.setMemo("Invalid payment: " + siteOrderIdentifier);

        }

        XMLOutputter out = new XMLOutputter();
        out.output(auth.build(), response.getOutputStream());

        logger.trace("End handleAuthorizationRequest");
    }
}
