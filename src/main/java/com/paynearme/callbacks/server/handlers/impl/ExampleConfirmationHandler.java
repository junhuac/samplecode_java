package com.paynearme.callbacks.server.handlers.impl;

import com.paynearme.callbacks.response.ConfirmationResponseBuilder;
import com.paynearme.callbacks.enums.Version;
import com.paynearme.callbacks.server.handlers.ConfirmationHandler;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * An example implementation of {@link com.paynearme.callbacks.server.handlers.ConfirmationHandler}. This will "confirm"
 * all requests sent to it.
 *
 * This call has some bonus features snuck in too. By setting various things for site_order_annotation you can get some
 * interesting (bad) behavior. "confirm_delay_{seconds}" will delay response for {seconds}. "confirm_bad_xml" will reply
 * with broken xml. "confirm_blank" gives a blank response. "confirm_redirect" redirects to /.
 */
public class ExampleConfirmationHandler implements ConfirmationHandler {
    @Override
    public void handleConfirmationRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Do some funky stuff that the echo server does!
        Map<String, String[]> params = request.getParameterMap();
        PrintWriter writer = response.getWriter();
        Version apiVersion = Version.getVersionFromString(params.get("version")[0]);
        try {
            String special = params.get("site_order_annotation")[0];
            if (special.startsWith("confirm_delay_")) {
                Thread.sleep(Integer.parseInt(special.substring(special.lastIndexOf('_') + 1)) * 1000);
            } else if (special.equals("confirm_bad_xml")) {
                writer.print("<result");
                writer.flush();
                return;
            } else if (special.equals("confirm_blank")) {
                return;
            } else if (special.equals("confirm_redirect")) {
                response.sendRedirect("/");
                return;
            }
        } catch (NullPointerException | InterruptedException e) {
            // Can't handle the annotation.
        }

        String identifier = params.get("pnm_order_identifier")[0];


        response.setStatus(HttpServletResponse.SC_OK);
        ConfirmationResponseBuilder confirm = new ConfirmationResponseBuilder(apiVersion);
        confirm.setPnmOrderIdentifier(identifier);
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        out.output(confirm.build(), writer);
    }
}
