package com.github.imdmk.automessage.platform.capability;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public record Capabilities(@Unmodifiable Set<Capability> supported) {

    public Capabilities {
        supported = supported.isEmpty()
                ? Set.of()
                : Set.copyOf(EnumSet.copyOf(supported));
    }

    public static Capabilities of(Capability... capabilities) {
        return new Capabilities(capabilities.length == 0
                ? Set.of()
                : EnumSet.copyOf(Arrays.asList(capabilities)));
    }

    public static Capabilities all() {
        return new Capabilities(EnumSet.allOf(Capability.class));
    }

    // A subtraction on purpose: a capability added later is supported by default on these
    // platforms and has to be argued out, rather than remembered in.
    public static Capabilities allExcept(Capability... unsupported) {
        final EnumSet<Capability> supported = EnumSet.allOf(Capability.class);
        supported.removeAll(Arrays.asList(unsupported));

        return new Capabilities(supported);
    }

    public boolean supports(Capability capability) {
        return supported.contains(capability);
    }
}
