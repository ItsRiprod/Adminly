package com.riprod.adminly.restart;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import javax.annotation.Nonnull;

public final class AutoRestartConfig {

    @Nonnull
    public static final BuilderCodec<AutoRestartConfig> CODEC = BuilderCodec.builder(AutoRestartConfig.class, AutoRestartConfig::new)
        .append(
            new KeyedCodec<>("Enabled", Codec.BOOLEAN),
            (o, v) -> o.enabled = v,
            o -> o.enabled
        )
        .add()
        .append(
            new KeyedCodec<>("RestartDelaySeconds", Codec.LONG),
            (o, v) -> o.restartDelaySeconds = v,
            o -> o.restartDelaySeconds
        )
        .add()
        .append(
            new KeyedCodec<>("MaxRetries", Codec.INTEGER),
            (o, v) -> o.maxRetries = v,
            o -> o.maxRetries
        )
        .add()
        .append(
            new KeyedCodec<>("RetryWindowSeconds", Codec.LONG),
            (o, v) -> o.retryWindowSeconds = v,
            o -> o.retryWindowSeconds
        )
        .add()
        .append(
            new KeyedCodec<>("CrashLogEnabled", Codec.BOOLEAN),
            (o, v) -> o.crashLogEnabled = v,
            o -> o.crashLogEnabled
        )
        .add()
        .append(
            new KeyedCodec<>("CrashLogFile", Codec.STRING),
            (o, v) -> o.crashLogFile = v,
            o -> o.crashLogFile
        )
        .add()
        .build();

    private boolean enabled = true;
    private long restartDelaySeconds = 10L;
    private int maxRetries = 3;
    private long retryWindowSeconds = 300L;
    private boolean crashLogEnabled = true;
    private String crashLogFile = "crashes.log";

    public boolean isEnabled() {
        return enabled;
    }

    public long getRestartDelaySeconds() {
        return restartDelaySeconds;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getRetryWindowSeconds() {
        return retryWindowSeconds;
    }

    public boolean isCrashLogEnabled() {
        return crashLogEnabled;
    }

    public String getCrashLogFile() {
        return crashLogFile;
    }
}
