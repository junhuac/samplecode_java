package com.paynearme.callbacks.response;

import com.paynearme.callbacks.enums.Version;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * Abstract class for building various callback responses.
 */
public abstract class AbstractResponseBuilder {
    protected Namespace namespace;
    protected Namespace xsiNamespace;
    protected Document document;
    protected Element rootElement;
    protected final Version version;

    public AbstractResponseBuilder(Version version) {
        this.version = version;
    }

    // Utility methods --------------------------------------------------------
    protected Element createElement(Element parent, String name) {
        Element e = new Element(name, namespace);
        if (parent != null) parent.addContent(e);
        return e;
    }

    protected Element createElement(Element parent, String name, String text) {
        Element e = createElement(parent, name);
        e.setText(text);
        return e;
    }

    /**
     * Build the XML response and return the document. From here a servlet can output the document via its OutputStream.
     * @return XML Document of the response
     */
    public Document build() {
        document = new Document();
        namespace = Namespace.getNamespace("t", "http://www.paynearme.com/api/pnm_xmlschema_" + version.name());
        xsiNamespace = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        return document;
    }
}
