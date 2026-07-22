package com.ecommerce.backend.exception.base;

import com.ecommerce.backend.exception.message.ErrorMessage;
import com.ecommerce.backend.exception.message.MessageType;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final MessageType messageType;

    public BaseException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
        this.messageType = errorMessage.getMessageType();
    }

    public BaseException(MessageType messageType) {
        this(new ErrorMessage(messageType, null));
    }
}
