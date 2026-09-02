package com.github.imdmk.automessage.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.title.Title;

import java.util.Collection;

final class AudienceGroup implements ForwardingAudience {

    private final Collection<Audience> members;

    private AudienceGroup(Collection<Audience> members) {
        this.members = members;
    }

    static Audience of(Collection<Audience> members) {
        // One recipient is not a group, and wrapping it would only add a hop between the
        // renderer and the player.
        return members.size() == 1 ? members.iterator().next() : new AudienceGroup(members);
    }

    @Override
    public Iterable<? extends Audience> audiences() {
        return members;
    }

    // Adventure's own forwarding takes showTitle apart into three sendTitlePart calls, which an
    // audience that implements showTitle and not the parts never sees - its titles would simply
    // stop arriving. Forwarded whole, a group behaves exactly like sending to each in turn.
    @Override
    public void showTitle(Title title) {
        for (final Audience member : members) {
            member.showTitle(title);
        }
    }
}
