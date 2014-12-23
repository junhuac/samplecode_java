package com.paynearme.callbacks.server;

import com.paynearme.callbacks.server.handlers.AuthorizationHandler;
import com.paynearme.callbacks.server.handlers.ConfirmationHandler;
import com.paynearme.callbacks.server.handlers.impl.ExampleAuthorizationHandler;
import com.paynearme.callbacks.server.handlers.impl.ExampleConfirmationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for PayNearMe callbacks. This is intended to be a starting point for
 * building callback handling to interact with PayNearMe.
 */
@WebServlet(
        name = "PayNearMeServlet",
        description = "PayNearMe Callback handling and reference implementation",
        urlPatterns = {"/authorize", "/confirm"})
public class PayNearMeServlet extends HttpServlet {

    private Logger logger;

    private final AuthorizationHandler authHandler;
    private final ConfirmationHandler confirmHandler;

    public PayNearMeServlet() {
        super();
        logger = LoggerFactory.getLogger(getClass());

        /* Provide your own handlers for the supported calls here */
        /* You could use DI here to provide the handlers          */
        authHandler = new ExampleAuthorizationHandler();
        confirmHandler = new ExampleConfirmationHandler();

        logger.info("================================================================================");
        logger.info("  PayNearMe Callback Bootstrap v{}", Constants.PNM_BOOTSTRAP_VERSION); // for debugging help.
        logger.info("  Using SITE_IDENTIFIER = '{}'", Constants.SITE_IDENTIFIER);
        logger.info("================================================================================");
    }

    /**
     * Handle the incoming request. Based on what is requested, call the appropriate handler. Also handles
     * generic exceptions and invalid signatures that are not handled by their own handler.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long requestStartTime = System.currentTimeMillis();

        logger.trace("Began doGet()");

        if (logger.isInfoEnabled()) {
            StringBuilder paramDump = new StringBuilder();
            for (String key : req.getParameterMap().keySet()) {
                paramDump.append(key).append("=").append(req.getParameter(key)).append("&");
            }

            if (paramDump.length() > 1) {
                logger.info("Incoming request: {}?{}", req.getRequestURL(), paramDump.substring(0, paramDump.length() - 1));
            } else {
                logger.info("Incoming request: {}?{}", req.getRequestURL());
            }
        }

        // Route and build our responses - no need for a default route as we define this servlet as only responding to
        // the calls /authorize and /confirm
        try {
            switch (req.getServletPath()) {
                case "/authorize":
                    logger.trace("Begin /authorize handler");
                    authHandler.handleAuthorizationRequest(req, resp); // responsible for validating signature itself.
                    logger.trace("End /authorize handler");
                    break;
                case "/confirm":
                    logger.trace("Begin /confirm handler");
                    SignatureUtils.validateSignature(req, Constants.SECRET); // validate signature BEFORE the handler.
                    confirmHandler.handleConfirmationRequest(req, resp);
                    logger.trace("End /confirm handler");
                    break;
            }
        } catch (InvalidSignatureException e) {
            /**
             *  InvalidSignatureException
             *  Invalid Signature is a special case of exception that throws an HTTP Error.  With the exception of
             *  Invalid Signature and Internal Server errors, it is expected that the callback response be properly
             *  formatted XML per the PayNearMe specification.

             *  This is a security exception and may highlight a configuration problem (wrong secret or siteIdentifier)
             *  OR it may highlight a possible payment injection from a source other than PayNearMe.  You may choose to
             *  notify your IT department when this error class is raised.  PayNearMe strongly recommends that your
             *  callback listeners be whitelisted to ONLY allow traffic from PayNearMe IP addresses.

             *  When this class of error is raised in a production environment you may choose to not respond to
             *  PayNearMe, which will trigger a timeout exception, leading to PayNearMe to retry the callbacks up to
             *  40 times.  If the error persists, callbacks will be suspended.

             *  In development environment this default message will aid with debugging.
             */

            logger.error("Invalid Signature", e);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("text/plain");
            resp.getWriter().println("Invalid Signature");
        } catch (Exception e) {
            /**
             *   Internal Server Error
             *   Internal Server Error is a special case of exception that throws an HTTP Error.  With the exception of
             *   Invalid Signature and Internal Server errors, it is expected that the callback response be properly
             *   formatted XML per the PayNearMe specification.

             *   When this class of error is raised in a production environment you may choose to not respond to
             *   PayNearMe, which will trigger a timeout exception, leading to PayNearMe to retry the callbacks up to
             *   40 times.  If the error persists, callbacks will be suspended.

             *   This error may highlight a server outage in your infrastructure. You may choose to notify your IT
             *   department when this error class is raised.
             */

            logger.error("Exception thrown", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/plain");
            resp.getWriter().println("Internal Server Error");
            //e.printStackTrace(resp.getWriter());
        }

        long requestDuration = System.currentTimeMillis() - requestStartTime;
        logger.info("Request handled in {}ms", requestDuration);
        if (requestDuration >= 6000) {
            logger.warn("Request was longer than 6 seconds (6000ms)!");
        }

        logger.trace("End doGet()");
    }

    // treat a POST as a GET.
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("POST request received, handling with doGet()");
        doGet(req, resp);
    }
}
