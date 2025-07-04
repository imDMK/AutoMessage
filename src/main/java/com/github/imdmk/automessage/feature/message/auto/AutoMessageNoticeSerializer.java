package com.github.imdmk.automessage.feature.message.auto;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.feature.message.auto.sound.AutoMessageSound;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoMessageNoticeSerializer implements ObjectSerializer<AutoMessageNotice> {

    @Override
    public boolean supports(@NotNull Class<? super AutoMessageNotice> type) {
        return AutoMessageNotice.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull AutoMessageNotice message, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.addCollection("notices", message.getNotices(), Notice.class);

        message.getSound().ifPresent(sound -> data.add("sound", sound, AutoMessageSound.class));
        message.getRequiredPermission().ifPresent(permission -> data.add("requiredPermission", permission, String.class));
        message.getRequiredGroup().ifPresent(group -> data.add("requiredGroup", group, String.class));
    }

    @Override
    public AutoMessageNotice deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        List<Notice> notices = data.getAsList("notices", Notice.class);

        AutoMessageSound sound = data.get("sound", AutoMessageSound.class);
        String requiredPermission = data.get("requiredPermission", String.class);
        String requiredGroup = data.get("requiredGroup", String.class);

        return AutoMessageNotice.builder()
                .notices(notices)
                .sound(sound)
                .requiredPermission(requiredPermission)
                .requiredGroup(requiredGroup)
                .build();
    }
}
