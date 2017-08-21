package com.im.netty.xmpp;

/**
 * XMPP Protocol Namespaces
 */
public interface Namespaces {

    /**
     * 	RFC 6121: XMPP IM
     */
    String CLIENT = "jabber:client";

    /**
     * XEP-0078: Non-SASL Authentication
     */
    String AUTH = "jabber:iq:auth";

    /**
     * 	RFC 6121: XMPP IM
     */
    String ROSTER = "jabber:iq:roster";

    /**
     * 	RFC 6121: XMPP IM
     */
    String SERVER = "jabber:server";

}
