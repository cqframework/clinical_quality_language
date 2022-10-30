package org.cqframework.fhir.utilities;

import java.io.File;
import java.net.URI;
import org.apache.commons.lang3.SystemUtils;

public class Uris {
    private Uris() {}

    private static final String FILE_UNC_PREFIX = "file:////";
    private static final String FILE_SCHEME = "file";

    public static URI getHead(URI uri) {
        String path = uri.getRawPath();
        if (path != null) {
            int index = path.lastIndexOf("/");
            if (index > -1) {
                return withPath(uri, path.substring(0, index));
            }

            return uri;
        }

        return uri;
    }

    public static URI withPath(URI uri, String path) {
        try {
            return URI.create((uri.getScheme() != null ? uri.getScheme() + ":" : "") + "//"
                    + createAuthority(uri.getRawAuthority()) + createPath(path)
                    + createQuery(uri.getRawQuery()) + createFragment(uri.getRawFragment()));
        } catch (Exception e) {
            return null;
        }
    }

    public static URI addPath(URI uri, String path) {
        return withPath(uri, stripTrailingSlash(uri.getRawPath()) + createPath(path));
    }

    public static URI parseOrNull(String uriString) {
        try {
            URI uri = new URI(uriString);
            if (SystemUtils.IS_OS_WINDOWS && FILE_SCHEME.equals(uri.getScheme())) {
                uri = new File(uri.getSchemeSpecificPart()).toURI();
            }

            return uri;
        } catch (Exception e) {
            return null;
        }
    }

    public static String toClientUri(URI uri) {
        if (uri == null) {
            return null;
        }

        String uriString = uri.toString();
        if (SystemUtils.IS_OS_WINDOWS && uriString.startsWith(FILE_UNC_PREFIX)) {
            uriString = uriString.replace(FILE_UNC_PREFIX, "file://");
        }

        return uriString;
    }

    private static String createAuthority(String rawAuthority) {
        return rawAuthority != null ? rawAuthority : "";
    }

    private static String stripTrailingSlash(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }

        return path;
    }

    private static String createPath(String pathValue) {
        return ensurePrefix("/", pathValue);
    }

    private static String createQuery(String queryValue) {
        return ensurePrefix("?", queryValue);
    }

    private static String createFragment(String fragmentValue) {
        return ensurePrefix("#", fragmentValue);
    }

    private static String ensurePrefix(String prefix, String value) {
        if (value == null || value.isEmpty()) {
            return "";
        } else if (value.startsWith(prefix)) {
            return value;
        } else {
            return prefix + value;
        }
    }
}
