package com.paynearme.callbacks.response;

import com.paynearme.callbacks.enums.Version;
import org.jdom2.Document;
import org.jdom2.Element;

/**
 * Created by ryan on 11/18/13.
 */
public class ConfirmationResponseBuilder extends AbstractResponseBuilder {
    private String pnmOrderIdentifier;
    private String pnmPaymentIdentifier;

    public ConfirmationResponseBuilder(Version version) {
        super(version);
    }

    public void setPnmOrderIdentifier(String pnmOrderIdentifier) {
        this.pnmOrderIdentifier = pnmOrderIdentifier;
    }

    public void setPnmPaymentIdentifier(String pnmPaymentIdentifier) {
        this.pnmPaymentIdentifier = pnmPaymentIdentifier;
    }

    @Override
    public Document build() {
        super.build();
        rootElement = createElement(null, "payment_confirmation_response");
        rootElement.addNamespaceDeclaration(xsiNamespace);
        rootElement.setAttribute("version", version.value);

        Element confirmation = createElement(rootElement, "confirmation");

        if (pnmOrderIdentifier != null) createElement(confirmation, "pnm_order_identifier", pnmOrderIdentifier);
        if (pnmPaymentIdentifier != null) createElement(confirmation, "pnm_payment_identifier", pnmPaymentIdentifier);

        document.addContent(rootElement);

        return document;
    }
}
