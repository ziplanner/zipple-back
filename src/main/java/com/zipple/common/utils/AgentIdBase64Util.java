package com.zipple.common.utils;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@Component
public class AgentIdBase64Util {

    public String encodeLong(Long agentId) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(agentId);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }

    public Long decodeLong(String encodedAgentId) {
        byte[] bytes = Base64.getUrlDecoder().decode(encodedAgentId);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getLong();
    }
}
