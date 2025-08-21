package com.example.ieumapi.widy.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum WidyScope {
    PRIVATE, GROUP, PUBLIC
}
