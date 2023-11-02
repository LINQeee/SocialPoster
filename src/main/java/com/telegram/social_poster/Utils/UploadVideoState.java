package com.telegram.social_poster.Utils;


import lombok.Getter;

@Getter
public enum UploadVideoState {
    NOT_STARTED(0),
    VIDEO_DOWNLOADED(1),
    TITLE_SET(2),
    DESCRIPTION_SET(3),
    TAGS_SET(4),
    PRIVACY_STATUS_SET(5);

    private final int number;

    UploadVideoState(int number) {
        this.number = number;
    }

    public static UploadVideoState fromInt(int value) {
        for (UploadVideoState enumValue : UploadVideoState.values()) {
            if (enumValue.getNumber() == value) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}

