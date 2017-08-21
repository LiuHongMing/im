package com.im.netty.xmpp;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.istack.internal.Nullable;

import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 全局地址，本地部分@域部分/资源部分
 * <p>
 * jid = [ localpart "@" ] domainpart [ "/" resourcepart ]
 */
public class JID {

    private static final LoadingCache<String, JID> cache = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, JID>() {
        @Override
        public JID load(String uri) throws Exception {
            return parse(uri);
        }
    });

    /**
     * 域
     */
    private final String domain;
    /**
     * 本地
     */
    @Nullable
    private final String local;
    /**
     * 资源
     */
    @Nullable
    private final String resource;

    private JID(final String domain, @Nullable final String local, @Nullable final String resource) {
        this.domain = checkNotNull(domain);
        this.local = local;
        this.resource = resource;
    }

    public static final JID jid(final String domain, @Nullable final String local, @Nullable final String resource) {
        final JID result = new JID(domain, local, resource);
        return result;
    }

    public static final JID jid(@Nullable String uri) throws ExecutionException {
        if ((Strings.isNullOrEmpty(uri)))
            return null;
        return cache.get(uri);
    }

    public static JID parse(@Nullable String uri) {
        String domain, local = null, resource = null;
        final int at    = uri.indexOf('@');
        final int slash = uri.indexOf('/', at + 1);
        if (at > 0) {
            local = uri.substring(0, at);
            if (slash > at + 1) {
                domain   = uri.substring(at + 1, slash);
                resource = uri.substring(slash + 1);
            } else {
                domain = uri.substring(at + 1);
            }
        } else {
            if (slash > 0) {
                domain   = uri.substring(0, slash);
                resource = uri.substring(slash + 1);
            } else {
                domain = uri;
            }
        }
        return new JID(domain, local, resource);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(domain, local, resource);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof JID) {
            final JID other = (JID) obj;
            return Objects.equal(domain, other.domain) && Objects.equal(local, other.local)
                    && Objects.equal(resource, other.resource);
        }
        return false;
    }

    public final JID getBareJID() {
        if (Strings.isNullOrEmpty(resource))
            return this;
        return new JID(domain, local, null);
    }

    public String getDomain() {
        return domain;
    }

    public String getLocal() {
        return local;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (local != null) {
            builder.append(local);
            builder.append('@');
        }
        builder.append(domain);
        if (resource != null) {
            builder.append('/');
            builder.append(resource);
        }
        return builder.toString();
    }
}
