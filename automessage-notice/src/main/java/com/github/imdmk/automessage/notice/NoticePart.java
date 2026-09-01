package com.github.imdmk.automessage.notice;

public sealed interface NoticePart
        permits ChatPart, ActionBarPart, TitlePart, SubtitlePart, TitleTimesPart, HideTitlePart,
                BossBarPart, SoundPart {

    String key();
}
