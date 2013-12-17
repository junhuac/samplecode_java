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
        authHandler = new ExampleAuthorizationHandler();
        confirmHandler = new ExampleConfirmationHandler();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.trace("Began doGet()");
        logger.info("GET received");

        // Route and build our responses
        try {
            switch (req.getServletPath()) {
                case "/authorize":
                    logger.trace("Begin /authorize handler");
                    authHandler.handleAuthorizationRequest(req, resp);
                    logger.trace("End /authorize handler");
                    break;
                case "/confirm":
                    logger.trace("Begin /confirm handler");
                    SignatureUtils.validateSignature(req, Constants.SECRET);
                    confirmHandler.handleConfirmationRequest(req, resp);
                    logger.trace("End /confirm handler");
                    break;
                default:
                    logger.error("Unsupported/unknown method: {}", req.getServletPath());
                    resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    resp.setContentType("text/plain");
                    resp.getWriter().println("Unsupported endpoint");
                    resp.getWriter().flush();
            }
        } catch (InvalidSignatureException e) {
            logger.error("Invalid Signature", e);
        } catch (Exception e) {
            logger.error("Exception thrown", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/plain");
            resp.getWriter().println("Internal Server Error");
            //e.printStackTrace(resp.getWriter());
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
