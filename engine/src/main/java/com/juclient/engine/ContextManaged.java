package com.juclient.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class ContextManaged {
    private final Context context;
}
