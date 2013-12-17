package com.paynearme.callbacks.response;


import com.paynearme.callbacks.enums.Version;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.io.IOException;

/**
 * Callback response and a builder for it.
 */

/* Example response:

<t:payment_authorization_response
  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
  xmlns:t='http://www.paynearme.com/api/pnm_xmlschema_v1_7'
  version='1.7'>
    <t:authorization>
      <t:pnm_order_identifier>83854712689</t:pnm_order_identifier>
      <t:accept_payment>yes</t:accept_payment>
      <t:receipt>This is the receipt</t:receipt>
      <t:memo>Mon Nov 22 10:21:03 -0800 2010</t:memo>
      <t:site_payment_identifier>SPI-123</t:site_payment_identifier>
    </t:authorization>
</t:payment_authorization_response>

 */

public class AuthorizationResponseBuilder extends AbstractResponseBuilder {

    // Authorization fields
    private String orderIdentifier;
    private boolean acceptPayment;
    private String receipt;
    private String memo;
    private String sitePaymentIdentifier;

    public AuthorizationResponseBuilder(Version version) {
        super(version);
    }

    public void setOrderIdentifier(String orderIdentifier) {
        this.orderIdentifier = orderIdentifier;
    }

    public void setAcceptPayment(boolean acceptPayment) {
        this.acceptPayment = acceptPayment;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setSitePaymentIdentifier(String sitePaymentIdentifier) {
        this.sitePaymentIdentifier = sitePaymentIdentifier;
    }

    @Override
    public Document build() {
        super.build();
        rootElement = new Element("payment_authorization_response", namespace);
        rootElement.addNamespaceDeclaration(xsiNamespace);
        rootElement.setAttribute(schemaLocation);
        rootElement.setAttribute("version", version.value);
        document.addContent(rootElement);

        Element authorization = createElement(rootElement, "authorization");
        createElement(authorization, "pnm_order_identifier", orderIdentifier);
        createElement(authorization, "accept_payment", acceptPayment ? "yes" : "no");
        if (receipt != null) createElement(authorization, "receipt", receipt);
        if (memo != null) createElement(authorization, "memo", memo);
        createElement(authorization, "site_payment_identifier", sitePaymentIdentifier);

        return document;
    }
}
