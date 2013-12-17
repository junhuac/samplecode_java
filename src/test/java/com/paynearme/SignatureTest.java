package com.paynearme;

import com.paynearme.callbacks.server.SignatureUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for signature generation/creation
 */
@RunWith(JUnit4.class)
public class SignatureTest {

    /*
     * This test is using the example from a document on creating signatures. There is possibly a "bug" in the document
     * where there is an extra space after the site_order_identifier, but we account for that in this test.
     */
    @Test
    public void signatureFromDocument() {
        String secret = "d33af566kp+6ddh4d";
        //payment_amount=74.07, site_identifier=S3360952499, site_order_identifier=BHA-9901, payment_currency=USD, version=1.8, timestamp=1345233767
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("payment_amount", new String[]{"74.07"});
        params.put("site_identifier", new String[]{"S3360952499"});
        params.put("site_order_identifier", new String[]{"BHA-9901 "}); // space added to order# so output matches document!
        params.put("payment_currency", new String[]{"USD"});
        params.put("version", new String[]{"1.8"});
        params.put("timestamp", new String[]{"1345233767"});

        Assert.assertEquals("355f51f7c46a67bfd0c47b677a9c925a", SignatureUtils.signature(secret, params));
    }
}
