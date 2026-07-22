# Adminly

Server administration utilities for Hytale.

Currently ships a single feature: **world auto-restart**.

## World auto-restart

Each Hytale world runs on its own thread. When one world crashes (an uncaught exception in its
tick loop), the engine isolates the failure to that thread, drains/disconnects its players, and
removes the world from the universe. Every other world keeps running, but the crashed world stays
down until an admin reloads it.

Adminly listens for that exceptional removal and reloads the crashed world automatically, after a
configurable delay, with a max-retries cap so a deterministically-crashing world does not loop
forever.

## Configuration

Written to `mods/Riprod_Adminly/config.json` on first launch (defaults shown):

```json
{
  "Enabled": true,
  "RestartDelaySeconds": 10,
  "MaxRetries": 3,
  "RetryWindowSeconds": 300
}
```

- `Enabled` - master switch for auto-restart.
- `RestartDelaySeconds` - how long to wait after a crash before reloading the world (the timeout).
- `MaxRetries` - consecutive restart attempts allowed before giving up on a world.
- `RetryWindowSeconds` - how long a restarted world must stay alive before its attempt counter
  resets to zero.

## Build

```
./gradlew build
```

Produces `build/libs/Adminly-<version>.jar`.
