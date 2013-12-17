package com.paynearme.callbacks.server.handlers.impl;

import com.paynearme.callbacks.response.ConfirmationResponseBuilder;
import com.paynearme.callbacks.enums.Version;
import com.paynearme.callbacks.server.handlers.ConfirmationHandler;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * {@inheritDoc}
 * <p/>
 * An example implementation of {@link com.paynearme.callbacks.server.handlers.ConfirmationHandler}. This will "confirm"
 * all requests sent to it. Only allow this to work once per unique order. You can use the signature as a key since the
 * request is timestamped.
 * <p/>
 * This call has some bonus features snuck in too. By setting various things for site_order_annotation you can get some
 * interesting (bad) behavior. "confirm_delay_{seconds}" will delay response for {seconds}. "confirm_bad_xml" will reply
 * with broken xml. "confirm_blank" gives a blank response. "confirm_redirect" redirects to /.
 */
public class ExampleConfirmationHandler implements ConfirmationHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleConfirmationRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.trace("Begin handleConfirmationRequest");
        logger.info("Handling /confirm with ExampleConfirmationHandler");

        // Do some extra functions that the 'echo server' does for debugging
        Map<String, String[]> params = request.getParameterMap();
        PrintWriter writer = response.getWriter();
        Version apiVersion = Version.getVersionFromString(params.get("version")[0]);
        try {
            String special = params.get("site_order_annotation")[0];
            if (special.startsWith("confirm_delay_")) {
                int delay = Integer.parseInt(special.substring(special.lastIndexOf('_') + 1)) * 1000;
                logger.info("Delaying response by {} seconds", delay);
                Thread.sleep(delay);
            } else if (special.equals("confirm_bad_xml")) {
                logger.info("Responding with bad/broken xml");
                writer.print("<result");
                writer.flush();
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

        // If the url contains the parameter test=true (part of the signed params too!) then we flag this.
        // Do not handle test=true requests as real requests.
        boolean isTest = request.getParameter("test") != null && Boolean.parseBoolean(request.getParameter("test"));
        if (isTest) {
            logger.info("This confirmation request is a TEST!");
        }

        String identifier = request.getParameter("site_order_identifier");
        String pnmOrderIdentifier = request.getParameter("pnm_order_identifier");
        String pnmPaymentIdentifier = request.getParameter("pnm_payment_identifier");
        String status = request.getParameter("status");

        /* You must lookup the pnm_order_identifier in your business system and
           prevent double posting. In the event of a duplicate, ignore the
           posting(do not reply) if you have already responded at least once for
           the pnm_order_identifier in question.
           No stub code is provided for this check, and is left as a
           responsibility of the implementor.
         */

        if ((pnmOrderIdentifier == null || pnmOrderIdentifier.equals(""))
                && (pnmPaymentIdentifier == null || pnmPaymentIdentifier.equals(""))) {
            logger.error("pnm_order_identifier or pnm_payment_identifier is empty or null, do not respond!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().println(
                    "Bad request: missing parameter pnm_order_identifier or pnm_payment_identifier");
            return;
        }

        if (status != null && status.equals("decline")) {
            logger.info("Status: declined, do not credit (site_order_identifier: {})", identifier);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        ConfirmationResponseBuilder confirm = new ConfirmationResponseBuilder(apiVersion);
        confirm.setPnmOrderIdentifier(pnmOrderIdentifier);
        //confirm.setPnmPaymentIdentifier(pnmPaymentIdentifier);
        XMLOutputter out = new XMLOutputter();
        out.output(confirm.build(), writer);

        logger.info(
                "Response sent for pnm_order_identifier: {} or pnm_payment_identifier: {}, site_order_identifier: {}",
                pnmOrderIdentifier, pnmPaymentIdentifier, identifier);

        /* Now that you have responded to a /confirm, you need to keep a record
           of this pnm_order_identifier and DO NOT respond to any other
           /confirm requests for that pnm_order_identifier.
          */

        logger.trace("End handleConfirmationRequest");
    }
}
