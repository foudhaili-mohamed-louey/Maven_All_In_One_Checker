# Tor Proxy Integration for Holehe and Gravatar

This document describes the Tor proxy integration for enhancing privacy and avoiding rate limits when using Holehe and Gravatar APIs.

## Overview

The application now supports routing HTTP requests through Tor's SOCKS5 proxy for:
- **Gravatar API**: Profile lookups and metadata retrieval
- **Holehe**: Email presence checking across multiple online services

### Benefits
- **Privacy**: Hide your IP address from target services
- **Rate Limit Avoidance**: Rotate Tor circuits to change exit nodes
- **Reliability**: Automatic fallback to direct connection if Tor is unavailable

## Configuration

### Environment Variables

Configure Tor integration using these environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `TOR_ENABLED` | Enable/disable Tor routing | `false` |
| `TOR_HOST` | Tor SOCKS5 proxy host | `127.0.0.1` |
| `TOR_PORT` | Tor SOCKS5 proxy port | `9050` |
| `TOR_CONNECTION_TIMEOUT_MS` | Connection timeout (milliseconds) | `30000` |
| `TOR_READ_TIMEOUT_MS` | Read timeout (milliseconds) | `30000` |
| `TOR_CIRCUIT_ROTATION_INTERVAL_MS` | Circuit rotation interval | `10000` |

### Example Configuration

```bash
# Enable Tor routing
export TOR_ENABLED=true

# Use custom Tor port (e.g., Tor Browser uses 9150)
export TOR_PORT=9150

# Adjust timeouts for slower Tor connections
export TOR_CONNECTION_TIMEOUT_MS=60000
export TOR_READ_TIMEOUT_MS=60000

# Rotate circuit every 30 seconds
export TOR_CIRCUIT_ROTATION_INTERVAL_MS=30000
```

## Installation & Setup

### 1. Install Tor

#### Ubuntu/Debian
```bash
sudo apt-get update
sudo apt-get install tor
sudo systemctl start tor
```

#### macOS (via Homebrew)
```bash
brew install tor
brew services start tor
```

#### Windows
Download and install from: https://www.torproject.org/download/

### 2. Configure Tor Control Port (Optional)

For circuit rotation to work, enable Tor control port:

Edit `/etc/tor/torrc` (Linux/macOS) or `torrc` in Tor Browser directory (Windows):

```
ControlPort 9051
CookieAuthentication 0
```

Restart Tor:
```bash
sudo systemctl restart tor
```

### 3. Verify Tor is Running

```bash
# Check if Tor SOCKS proxy is listening
netstat -an | grep 9050

# Or test with curl
curl --socks5 127.0.0.1:9050 https://check.torproject.org/api/ip
```

## Usage

### Basic Usage (Tor Disabled)

By default, Tor is disabled. The application works normally:

```java
EmailIntelligenceService service = new EmailIntelligenceServiceImpl();
```

### Enable Tor via Environment

```bash
export TOR_ENABLED=true
java -jar application.jar
```

### Enable Tor Programmatically

```java
import config.TorProxyConfig;
import services.EmailIntelligenceServiceImpl;

// Create Tor configuration
TorProxyConfig torConfig = new TorProxyConfig(
    "127.0.0.1",  // Tor host
    9050,         // Tor port
    true          // Enable Tor
);

// Create service with Tor support
EmailIntelligenceService service = new EmailIntelligenceServiceImpl(torConfig);
```

## Holehe Integration

### Command-Line Mode (Recommended)

Install Holehe CLI tool:

```bash
pip install holehe
```

The application will automatically detect and use Holehe CLI when available.

### Manual API Mode

If Holehe CLI is not installed, the application uses a simplified implementation that checks a subset of services via direct HTTP requests.

### Rate Limiting

To avoid overwhelming services:
- Maximum 10 services checked per email
- 500ms delay between requests
- Circuit rotation every 10 seconds (configurable)

## Circuit Rotation

Circuit rotation changes the Tor exit node, providing a fresh IP address:

### Automatic Rotation

Circuits rotate automatically based on `TOR_CIRCUIT_ROTATION_INTERVAL_MS`.

### Manual Rotation

```java
TorHttpClient torClient = new TorHttpClient(torConfig);
torClient.forceRotateCircuit();
```

### Requirements

- Tor control port must be enabled (port 9051)
- Control port authentication must be configured

If control port is not available, circuit rotation will fail gracefully without affecting requests.

## Security Considerations

### Tor Safety
- **DO NOT** send sensitive data through Tor unless you understand the risks
- Exit nodes can potentially monitor unencrypted traffic
- Use HTTPS URLs to encrypt data in transit

### Rate Limiting
- Even with Tor, respect service rate limits
- Excessive requests may trigger blocks on exit nodes
- Use reasonable delays between requests

### Fallback Behavior
- If Tor connection fails, requests fall back to direct connection (if configured)
- Application continues to function even if Tor is unavailable
- Errors are logged but don't crash the application

## Testing

Run tests to verify integration:

```bash
mvn test
```

Tests include:
- TorProxyConfig configuration tests
- TorHttpClient proxy setup tests
- GravatarCollector with Tor integration
- HoleheClient service checking
- ServicePresenceChecker integration

## Troubleshooting

### Tor Connection Failed

**Problem**: Requests fail with connection errors

**Solutions**:
1. Verify Tor is running: `systemctl status tor`
2. Check Tor is listening: `netstat -an | grep 9050`
3. Test Tor connection: `curl --socks5 127.0.0.1:9050 https://check.torproject.org/api/ip`
4. Check firewall settings

### Circuit Rotation Not Working

**Problem**: Warning message "Failed to rotate Tor circuit"

**Solutions**:
1. Enable control port in `/etc/tor/torrc`
2. Set `ControlPort 9051` and `CookieAuthentication 0`
3. Restart Tor service
4. Circuit rotation is optional - requests still work without it

### Slow Performance

**Problem**: Requests take a long time

**Solutions**:
1. Increase timeouts: `TOR_CONNECTION_TIMEOUT_MS` and `TOR_READ_TIMEOUT_MS`
2. Tor connections are inherently slower than direct connections
3. Consider disabling Tor for local development
4. Use circuit rotation to find faster exit nodes

### Holehe Not Found

**Problem**: Holehe CLI not detected

**Solutions**:
1. Install Holehe: `pip install holehe`
2. Verify installation: `holehe --help`
3. Application falls back to manual API mode if Holehe is not installed

## Architecture

### Components

1. **TorProxyConfig**: Configuration management
2. **TorHttpClient**: HTTP client with Tor SOCKS5 proxy support
3. **HoleheClient**: Holehe integration wrapper
4. **GravatarCollector**: Gravatar API with Tor support (updated)
5. **ServicePresenceChecker**: Service detection with Holehe (updated)
6. **EmailIntelligenceServiceImpl**: Main service orchestrator (updated)

### Request Flow

```
EmailIntelligenceServiceImpl
    ↓
ServicePresenceChecker / GravatarCollector
    ↓
TorHttpClient
    ↓
Tor SOCKS5 Proxy (if enabled)
    ↓
Internet Services (Holehe targets, Gravatar)
```

## API Reference

### TorProxyConfig

```java
// Create from environment
TorProxyConfig config = TorProxyConfig.fromEnvironment();

// Create with defaults
TorProxyConfig config = new TorProxyConfig();

// Create with custom settings
TorProxyConfig config = new TorProxyConfig(
    "127.0.0.1",  // host
    9050,         // port
    true          // enabled
);
```

### TorHttpClient

```java
TorHttpClient client = new TorHttpClient(config);

// Execute request through Tor
Request request = new Request.Builder()
    .url("https://example.com")
    .build();
Response response = client.execute(request);

// Execute without Tor
Response response = client.executeDirect(request);

// Rotate circuit
client.forceRotateCircuit();
```

### HoleheClient

```java
HoleheClient holehe = new HoleheClient(torClient, useCommandLine);
ServicePresence presence = holehe.checkEmail("user@example.com");
```

## License

This integration maintains the same license as the main project.
