package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.viewer.Viewer;

import java.util.Collection;

public record DispatchViewersTarget(
        Collection<Viewer> viewers
) implements DispatchTarget {

    @Override
    public Collection<Viewer> recipients() {
        return viewers;
    }
}
