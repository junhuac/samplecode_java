package com.paynearme.callbacks.response;

import com.paynearme.callbacks.enums.Version;
import org.jdom2.Document;

/**
 * Created by ryan on 11/18/13.
 */
public class ConfirmationResponseBuilder extends AbstractResponseBuilder {
    private String pnmOrderIdentifier;

    public ConfirmationResponseBuilder(Version version) {
        super(version);
    }

    public void setPnmOrderIdentifier(String pnmOrderIdentifier) {
        this.pnmOrderIdentifier = pnmOrderIdentifier;
    }

    @Override
    public Document build() {
        super.build();
        rootElement = createElement(null, "payment_confirmation_response");
        rootElement.addNamespaceDeclaration(xsiNamespace);
        rootElement.setAttribute("version", version.value);
        createElement(rootElement, "pnm_order_identifier", pnmOrderIdentifier);

        document.addContent(rootElement);

        return document;
    }
}
