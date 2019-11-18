package com.dev.kotlimultifilechooser;

public enum ReturnType {
    IMAGES(1),
    VIDEOS(2),
    AUDIOS(3),
    DOCUMENTS(4);


    private final int Value;

    ReturnType(final int value)
    {
        Value = value;
    }

    public int getValue() {
        return Value;
    }
}
