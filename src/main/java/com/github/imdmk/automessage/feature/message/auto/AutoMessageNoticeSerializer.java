package com.github.imdmk.automessage.feature.message.auto;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

public class AutoMessageNoticeSerializer implements ObjectSerializer<AutoMessageNotice> {

    @Override
    public boolean supports(@NotNull Class<? super AutoMessageNotice> type) {
        return AutoMessageNotice.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull AutoMessageNotice message, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        Notice notice = message.getNotice();

        data.add("notice", notice, Notice.class);

        message.getRequiredPermission()
                .ifPresent(permission -> data.add("requiredPermission", permission, String.class));

        message.getRequiredGroup()
                .ifPresent(group -> data.add("requiredGroup", group, String.class));
    }

    @Override
    public AutoMessageNotice deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        Notice notice = data.get("notice", Notice.class);

        String requiredPermission = data.get("requiredPermission", String.class);
        String requiredGroup = data.get("requiredGroup", String.class);

        return AutoMessageNotice.builder()
                .notice(notice)
                .requiredPermission(requiredPermission)
                .requiredGroup(requiredGroup)
                .build();
    }
}
