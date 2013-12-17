package com.paynearme.callbacks.response;

import com.paynearme.callbacks.enums.Version;
import org.jdom2.Document;

/**
 * Builder for the PendingPaymentResponse.
 */
public class PendingPaymentResponseBuilder extends AbstractResponseBuilder {
    private String pnmOrderIdentifier;

    public PendingPaymentResponseBuilder(Version version) {
        super(version);
    }

    public void setPnmOrderIdentifier(String pnmOrderIdentifier) {
        this.pnmOrderIdentifier = pnmOrderIdentifier;
    }

    @Override
    public Document build() {
        super.build();
        rootElement = createElement(null, "pending_payment_response");
        rootElement.addNamespaceDeclaration(xsiNamespace);
        rootElement.setAttribute("version", version.value);
        createElement(rootElement, "pnm_order_identifier", pnmOrderIdentifier);

        document.addContent(rootElement);

        return document;
    }
}
