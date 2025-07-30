# SSL Certificate Directory

This directory contains SSL certificates and private keys for the Tactical Command Hub application.

## File Structure

### Production Certificates (DO NOT COMMIT TO VERSION CONTROL)
- `tactical-command-hub.crt` - SSL certificate for the domain
- `tactical-command-hub.key` - Private key (KEEP SECURE)
- `tactical-command-hub-chain.crt` - Certificate chain file
- `dhparam.pem` - Diffie-Hellman parameters for enhanced security

### Development Certificates
- `dev-cert.crt` - Self-signed certificate for development
- `dev-cert.key` - Development private key
- `ca-certificates.crt` - Certificate Authority bundle

## Security Guidelines

### Certificate Management
1. **Never commit private keys to version control**
2. **Use strong encryption (RSA 2048-bit minimum, prefer RSA 4096-bit or ECDSA)**
3. **Regularly rotate certificates (recommended: annually)**
4. **Monitor certificate expiration dates**

### File Permissions
```bash
# Set appropriate permissions for certificate files
chmod 600 *.key      # Private keys - owner read/write only
chmod 644 *.crt      # Certificates - owner read/write, group/others read
chmod 644 *.pem      # Parameter files - readable by all
```

### Development Setup
For local development, you can generate self-signed certificates:

```bash
# Generate private key
openssl genrsa -out dev-cert.key 2048

# Generate certificate signing request
openssl req -new -key dev-cert.key -out dev-cert.csr \
  -subj "/C=US/ST=CA/L=San Francisco/O=Tactical Command Hub/OU=Development/CN=localhost"

# Generate self-signed certificate
openssl x509 -req -in dev-cert.csr -signkey dev-cert.key -out dev-cert.crt -days 365 \
  -extensions v3_req -extfile <(cat <<EOF
[v3_req]
keyUsage = keyEncipherment, dataEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[alt_names]
DNS.1 = localhost
DNS.2 = tactical-command-hub.local
IP.1 = 127.0.0.1
IP.2 = ::1
EOF
)

# Generate DH parameters (for enhanced security)
openssl dhparam -out dhparam.pem 2048
```

### Production Setup
For production deployment:

1. **Obtain certificates from a trusted Certificate Authority**
2. **Use Let's Encrypt for automated certificate management**
3. **Implement certificate pinning where appropriate**
4. **Configure HSTS and security headers**

### Certificate Validation
Validate your certificates before deployment:

```bash
# Check certificate details
openssl x509 -in tactical-command-hub.crt -text -noout

# Verify certificate chain
openssl verify -CAfile ca-certificates.crt tactical-command-hub.crt

# Test SSL configuration
openssl s_client -connect tactical-command-hub.local:443 -servername tactical-command-hub.local
```

## Docker Integration

When using Docker, mount this directory as a volume:

```yaml
services:
  nginx:
    volumes:
      - ./ssl:/etc/nginx/ssl:ro
```

## Backup and Recovery

### Backup Strategy
1. **Securely backup private keys to encrypted storage**
2. **Store certificates in configuration management system**
3. **Document certificate details and expiration dates**
4. **Maintain certificate revocation procedures**

### Recovery Procedures
1. **Have certificate replacement process documented**
2. **Test certificate deployment in staging environment**
3. **Maintain emergency contact information for CA**
4. **Keep backup certificates for immediate deployment**

## Monitoring and Alerts

Set up monitoring for:
- Certificate expiration (alert 30 days before expiry)
- SSL configuration changes
- Certificate validation failures
- Cipher suite compatibility

## Compliance

Ensure certificates meet organizational security requirements:
- **Encryption strength standards**
- **Certificate authority approval**
- **Key management policies**
- **Audit trail requirements**

---

**IMPORTANT SECURITY NOTE**: This directory should be excluded from version control. 
Add `ssl/*.key` and `ssl/*.pem` to your `.gitignore` file to prevent accidental commits of private keys.
