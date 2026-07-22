package com.ecommerce.backend.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionDetail<E> {

    private String path;
    private Instant timestamp;
    private E message;
}
