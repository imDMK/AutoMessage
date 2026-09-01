package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.viewer.Viewer;

import java.util.Collection;
import java.util.List;

public interface DispatchTarget {

    Collection<Viewer> recipients();

    static DispatchTarget viewer(Viewer viewer) {
        return new DispatchViewersTarget(List.of(viewer));
    }

    static DispatchTarget viewers(Collection<Viewer> viewers) {
        return new DispatchViewersTarget(viewers);
    }
}
