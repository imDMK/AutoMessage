package com.github.imdmk.automessage.platform.placeholder;

import com.github.imdmk.automessage.platform.viewer.Viewer;

public interface ExternalPlaceholderResolver {

    String resolve(Viewer viewer, String token);

    String resolveWithoutViewer(String token);

    boolean available();

    static ExternalPlaceholderResolver disabled() {
        return new ExternalPlaceholderResolver() {

            @Override
            public String resolve(Viewer viewer, String token) {
                return token;
            }

            @Override
            public String resolveWithoutViewer(String token) {
                return token;
            }

            @Override
            public boolean available() {
                return false;
            }
        };
    }
}
