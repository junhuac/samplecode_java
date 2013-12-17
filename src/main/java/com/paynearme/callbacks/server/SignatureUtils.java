package com.paynearme.callbacks.server;



import com.google.common.collect.ImmutableSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Utilities related to signature validation
 */
public class SignatureUtils {
    /**
     * Method to verify signature for incoming requests. Any request failing
     * the check should be served an error and the request should not be
     * handled.
     *
     * @param req
     * @param secret Shared secret key/token.
     * @throws com.paynearme.callbacks.server.InvalidSignatureException if the signature is not valid.
     */
    public static void validateSignature(HttpServletRequest req, String secret) throws InvalidSignatureException {
        if (Constants.IGNORE_KEY_CHECK) return;

        // Verify the signature
        try {
            Map<String, String[]> params = new HashMap<>(req.getParameterMap());
            String signature = req.getParameter("signature");

            String expected = signature(secret, params);
            if (!expected.equals(signature)) {
                throw new InvalidSignatureException(signature, expected, params);
            }
        } catch (NullPointerException e) {
            throw new InvalidSignatureException();
        }
    }


    /**
     * Ignored parameters - these are not used in the signature calculation.
     */
    private static final Set<String> IGNORE_PARAMS;

    static {
        IGNORE_PARAMS = ImmutableSet.of("signature", "action", "call", "controller", "fp", "print_buttons");
    }

    /**
     * Generate a signature for a request. Can be used for verification or creating requests.
     * <p/>
     *
     * @param secret Shared secret token
     * @param params List of parameters and values
     * @return MD5 hex string signature.
     */
    public static String signature(String secret, Map<String, String[]> params) {
        Logger logger = LoggerFactory.getLogger(SignatureUtils.class);
        // Sort the list by parameter name
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder string = new StringBuilder();
        logger.debug("Signature parameters: ");
        for (String key : keys) {
            if (!IGNORE_PARAMS.contains(key)) {
                logger.debug("\t{}", key);
                string.append(key).append(params.get(key)[0]);
            } else {
                logger.debug("\t[ignored] {}", key);
            }
        }
        string.append(secret);
        logger.debug("\tsecret: '{}'", secret);

        logger.debug("String to sign: {}", string.toString());
        return DigestUtils.md5Hex(string.toString());
    }

}
