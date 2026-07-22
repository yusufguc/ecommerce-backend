package com.ecommerce.backend.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {

    private final MessageType messageType;
    private final String detail;

    public String prepareErrorMessage() {
        if (detail == null) {
            return messageType.getMessage();
        }
        return messageType.getMessage() + " : " + detail;
    }
}
