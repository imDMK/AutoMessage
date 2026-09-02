package com.github.imdmk.automessage.platform.viewer;

@FunctionalInterface
public interface ViewerFactory<S> {

    Viewer of(S sender);
}
