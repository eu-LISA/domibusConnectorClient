/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.client.link.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

/**
 * The WsPolicyLoader class is responsible for loading a policy from a given resource and creating a
 * WSPolicyFeature object with the loaded policy.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class WsPolicyLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsPolicyLoader.class);
    private final Resource wsPolicy;

    public WsPolicyLoader(Resource resource) {
        this.wsPolicy = resource;
    }

    /**
     * Loads a policy from a given resource and creates a WSPolicyFeature object with the loaded
     * policy.
     *
     * @return the created WSPolicyFeature object with the loaded policy
     * @throws UncheckedIOException    if the ws policy cannot be read
     * @throws WsPolicyLoaderException if the ws policy cannot be read or cannot be parsed
     */
    public WSPolicyFeature loadPolicyFeature() {
        LOGGER.debug("Loading policy from resource: [{}]", wsPolicy);
        var policyFeature = new WSPolicyFeature();
        policyFeature.setEnabled(true);

        InputStream is;
        try {
            is = wsPolicy.getInputStream();
        } catch (IOException ioe) {
            throw new UncheckedIOException(
                String.format("ws policy [%s] cannot be read!", wsPolicy),
                ioe
            );
        }
        if (is == null) {
            throw new WsPolicyLoaderException(
                String.format("ws policy [%s] cannot be read! InputStream is nulL!", wsPolicy)
            );
        }
        List<Element> policyElements = new ArrayList<>();
        try {
            var element = StaxUtils.read(is).getDocumentElement();
            LOGGER.debug("adding policy element [{}]", element);
            policyElements.add(element);
        } catch (XMLStreamException ex) {
            throw new WsPolicyLoaderException("cannot parse policy " + wsPolicy, ex);
        }
        policyFeature.setPolicyElements(policyElements);
        return policyFeature;
    }
}
