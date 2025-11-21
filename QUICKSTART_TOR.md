# Quick Start: Using Tor Integration

This guide will help you quickly get started with Tor integration for Holehe and Gravatar.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Tor installed and running (optional, required only if you want to use Tor)

## Option 1: Use Without Tor (Default)

The application works out of the box without Tor:

```bash
mvn clean package
mvn javafx:run
```

All features work normally with direct internet connections.

## Option 2: Enable Tor Integration

### Step 1: Install Tor

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install tor
sudo systemctl start tor
```

**macOS:**
```bash
brew install tor
brew services start tor
```

**Windows:**
Download from https://www.torproject.org/download/

### Step 2: Verify Tor is Running

```bash
# Check if Tor SOCKS proxy is listening on port 9050
netstat -an | grep 9050

# Or test with curl
curl --socks5 127.0.0.1:9050 https://check.torproject.org/api/ip
```

### Step 3: Enable Tor in Your Application

**Option A: Using Environment Variables**

```bash
# Linux/macOS
export TOR_ENABLED=true
mvn javafx:run

# Windows (PowerShell)
$env:TOR_ENABLED="true"
mvn javafx:run
```

**Option B: Using Configuration File**

1. Copy the example configuration:
   ```bash
   cp tor-config.example.env .env
   ```

2. Edit `.env` and set:
   ```
   TOR_ENABLED=true
   ```

3. Load the environment and run:
   ```bash
   # Linux/macOS
   export $(cat .env | xargs)
   mvn javafx:run
   ```

### Step 4: (Optional) Enable Circuit Rotation

For circuit rotation to work, enable Tor control port:

1. Edit Tor configuration:
   ```bash
   # Linux/macOS
   sudo nano /etc/tor/torrc
   
   # Add these lines:
   ControlPort 9051
   CookieAuthentication 0
   ```

2. Restart Tor:
   ```bash
   sudo systemctl restart tor
   ```

## Configuration Options

| Environment Variable | Description | Default |
|---------------------|-------------|---------|
| `TOR_ENABLED` | Enable Tor routing | `false` |
| `TOR_HOST` | Tor proxy host | `127.0.0.1` |
| `TOR_PORT` | Tor SOCKS port | `9050` |
| `TOR_CONNECTION_TIMEOUT_MS` | Connection timeout | `30000` |
| `TOR_READ_TIMEOUT_MS` | Read timeout | `30000` |
| `TOR_CIRCUIT_ROTATION_INTERVAL_MS` | Circuit rotation interval | `10000` |

See `tor-config.example.env` for detailed examples.

## Programmatic Usage

```java
import config.TorProxyConfig;
import services.EmailIntelligenceServiceImpl;

// Create with Tor enabled
TorProxyConfig config = new TorProxyConfig(
    "127.0.0.1",  // Tor host
    9050,         // Tor port
    true          // Enable Tor
);

EmailIntelligenceService service = new EmailIntelligenceServiceImpl(config);

// Or use environment variables
TorProxyConfig config = TorProxyConfig.fromEnvironment();
EmailIntelligenceService service = new EmailIntelligenceServiceImpl(config);
```

## Troubleshooting

### "Connection refused" Error
- **Problem**: Application can't connect to Tor
- **Solution**: Verify Tor is running with `systemctl status tor`

### Requests are Slow
- **Problem**: Tor connections are slower than direct
- **Solution**: This is normal. Increase timeouts or disable Tor for development

### Circuit Rotation Not Working
- **Problem**: Warning about circuit rotation failure
- **Solution**: Enable Tor control port (see Step 4) - this is optional

## What Gets Routed Through Tor?

When `TOR_ENABLED=true`:
- ✅ Gravatar API requests
- ✅ Holehe service presence checks
- ❌ Application UI and local resources

## Performance Considerations

- Tor adds latency (typically 1-3 seconds per request)
- Circuit rotation helps avoid rate limits
- For local development, keep `TOR_ENABLED=false`
- For production/scraping, enable Tor to protect IP

## Security Notes

- Tor provides IP anonymity but doesn't encrypt unencrypted traffic
- All API requests use HTTPS for encryption
- Validate that exit nodes are trusted for sensitive operations

## Need Help?

See full documentation in `TOR_INTEGRATION.md`
