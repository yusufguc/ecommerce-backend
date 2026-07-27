package com.ecommerce.backend.exception.message;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageType {

    // --- GENERAL & VALIDATION (1000-1999) ---
    GENERAL_EXCEPTION("GEN-1000", "Beklenmeyen bir hata oluştu.", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_EXCEPTION("GEN-1001", "Doğrulama hatası.", HttpStatus.BAD_REQUEST),
    TOO_MANY_REQUESTS("GEN-1002", "Çok fazla istek gönderildi, lütfen bir dakika sonra tekrar deneyin.", HttpStatus.TOO_MANY_REQUESTS),

    // --- AUTH & USER (2000-2999) ---
    EMAIL_ALREADY_EXISTS("USR-2000", "Bu email zaten kayıtlı.", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("USR-2001", "Geçersiz email veya şifre.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("USR-2002", "Bu işlem için yetkin yok.", HttpStatus.FORBIDDEN),
    INVALID_REFRESH_TOKEN("USR-2003", "Geçersiz veya süresi dolmuş refresh token.", HttpStatus.UNAUTHORIZED),

    // --- CATEGORY (3000-3999) ---
    CATEGORY_NOT_FOUND("CAT-3000", "Kategori bulunamadı.", HttpStatus.NOT_FOUND),
    CATEGORY_ALREADY_EXISTS("CAT-3001", "Bu isimde bir kategori zaten mevcut.", HttpStatus.CONFLICT),

    // --- PRODUCT (4000-4999) ---
    PRODUCT_NOT_FOUND("PRD-4000", "Ürün bulunamadı.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK("PRD-4001", "Stok miktarı negatif olamaz.", HttpStatus.CONFLICT),

    // --- ORDER (5000-5999) ---
    ORDER_NOT_FOUND("ORD-5000", "Sipariş bulunamadı.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    MessageType(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
