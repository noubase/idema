package com.noubase.core.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;


public final class TokenHandler<U extends ExpirableUserDetails> {

    private static final String HMAC_ALGORITHM = "HmacSHA512";//"HmacSHA256";
    private static final String SEPARATOR = ".";
    private static final String SEPARATOR_SPLITTER = "\\.";
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @NotNull
    private final Mac hmac;

    private final Class<U> uClass;

    public TokenHandler(@NotNull byte[] secretKey, @NotNull Class<U> uClass) {
        try {
            hmac = Mac.getInstance(HMAC_ALGORITHM);
            hmac.init(new SecretKeySpec(secretKey, HMAC_ALGORITHM));
            this.uClass = uClass;
        } catch (@NotNull NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("failed to initialize HMAC: " + e.getMessage(), e);
        }
    }

    public ExpirableUserDetails parseUserFromToken(@NotNull String token) {
        final String[] parts = token.split(SEPARATOR_SPLITTER);
        if (parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0) {
            try {
                final byte[] userBytes = fromBase64(parts[0]);
                boolean validHash = Arrays.equals(createHmac(userBytes), fromBase64(parts[1]));
                if (validHash) {
                    final ExpirableUserDetails user = fromJSON(userBytes);
                    if (new Date().getTime() < user.getExpires()) {
                        return user;
                    }
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    @NotNull
    public String createTokenForUser(ExpirableUserDetails user) throws JsonProcessingException {
        byte[] userBytes = toJSON(user);
        byte[] hash = createHmac(userBytes);
        return toBase64(userBytes) + SEPARATOR + toBase64(hash);
    }


    private ExpirableUserDetails fromJSON(@NotNull final byte[] userBytes) {
        try {
            return mapper.readValue(new ByteArrayInputStream(userBytes), uClass);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private byte[] toJSON(ExpirableUserDetails user) {
        try {
            return mapper.writeValueAsBytes(user);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String toBase64(byte[] content) {
        return DatatypeConverter.printBase64Binary(content);
    }

    private byte[] fromBase64(String content) {
        return DatatypeConverter.parseBase64Binary(content);
    }

    // synchronized to guard internal hmac object
    private synchronized byte[] createHmac(byte[] content) {
        return hmac.doFinal(content);
    }

}
