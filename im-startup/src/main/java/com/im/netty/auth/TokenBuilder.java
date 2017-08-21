package com.im.netty.auth;

import com.im.netty.xmpp.JID;

public class TokenBuilder {

    public static Token token(String uri) {
        JID jid = JID.parse(uri);
        return new Token(jid.getLocal(), jid.getDomain());
    }

    public static Token token(String local, String domain) {
        return new Token(local, domain);
    }

}
