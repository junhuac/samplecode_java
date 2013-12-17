package com.paynearme;


import com.paynearme.callbacks.enums.Version;
import com.paynearme.callbacks.response.AuthorizationResponseBuilder;
import com.paynearme.callbacks.response.ConfirmationResponseBuilder;
import org.jdom2.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests responsebuilders
 */
@RunWith(JUnit4.class)
public class ResponseTest {

    @Test
    public void buildAuthorizationResponse() throws Exception {
        AuthorizationResponseBuilder resp = new AuthorizationResponseBuilder(Version.v2_0);
        resp.setReceipt("receipt text");
        resp.setAcceptPayment(true);
        resp.setMemo("memo text");
        resp.setOrderIdentifier("order-123");
        resp.setSitePaymentIdentifier("site-123");
        Document doc = resp.build();

//        XPathExpression<Element> xpath = XPathFactory.instance().compile("/t:pnm_authorization_response/t:authorization/t:authorization", Filters.element(), null, doc.getNamespacesIntroduced());
//        List<Element> authElements = xpath.evaluate(doc);
//        for (Element e: authElements) {
//            if (e.getName().equals("t:pnm_order_identifier")) Assert.assertEquals("order-123", e.getText());
//        }
    }

    @Test
    public void buildConfirmationResponse() throws Exception {
        ConfirmationResponseBuilder resp = new ConfirmationResponseBuilder(Version.v2_0);

    }
}
