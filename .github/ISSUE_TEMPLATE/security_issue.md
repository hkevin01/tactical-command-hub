---
name: Security Issue
about: Report a security vulnerability (Please use private channels for sensitive issues)
title: '[SECURITY] '
labels: 'security, critical, needs-triage'
assignees: ''

---

## ⚠️ SECURITY NOTICE ⚠️
**IMPORTANT:** If this is a critical security vulnerability that could compromise system security, please:
1. **DO NOT** post details in this public issue
2. Contact the maintainers privately via email
3. Use encrypted communication if possible
4. Allow time for responsible disclosure

## Security Issue Type
- [ ] Authentication vulnerability
- [ ] Authorization bypass
- [ ] Data exposure/leak
- [ ] Injection vulnerability (SQL, XSS, etc.)
- [ ] Cryptographic weakness
- [ ] Dependency vulnerability
- [ ] Configuration security issue
- [ ] Other: ___________

## Severity Assessment
**CVSS Score (if known):** ___/10

**Severity Level:**
- [ ] Critical (9.0-10.0) - Immediate action required
- [ ] High (7.0-8.9) - Fix within 7 days
- [ ] Medium (4.0-6.9) - Fix within 30 days
- [ ] Low (0.1-3.9) - Fix in next release cycle

## Affected Components
- [ ] Authentication system
- [ ] Authorization/RBAC
- [ ] Database layer
- [ ] API endpoints
- [ ] User interface
- [ ] Configuration files
- [ ] Dependencies
- [ ] Docker configuration
- [ ] CI/CD pipeline

## Description
Provide a clear description of the security issue. **Avoid posting exploit code or detailed attack vectors in public issues.**

## Impact Analysis
**What could an attacker achieve?**
- [ ] Data breach/exposure
- [ ] Privilege escalation
- [ ] System compromise
- [ ] Denial of service
- [ ] Data manipulation
- [ ] Unauthorized access

**Who is affected?**
- [ ] All users
- [ ] Authenticated users only
- [ ] Administrative users
- [ ] Specific user roles: ___________
- [ ] System administrators

## Environment Information
**Version:** 
**Environment:** 
- [ ] Production
- [ ] Staging
- [ ] Development
- [ ] Testing

## Steps to Reproduce (General)
Provide general steps without sensitive details:
1. Step 1
2. Step 2
3. Step 3

## Mitigation Recommendations
If you have suggestions for fixing or mitigating this issue:

**Immediate Mitigations:**
- [ ] Configuration change
- [ ] Access restriction
- [ ] Service restart
- [ ] Temporary workaround: ___________

**Long-term Fixes:**
- [ ] Code changes required
- [ ] Dependency updates needed
- [ ] Architecture changes needed
- [ ] Policy/procedure updates needed

## References
- Related CVE: ___________
- Security advisories: ___________
- Documentation: ___________

## Reporter Information
**Contact Method:** (Email preferred for security issues)
**Timeline:** When do you expect this to be addressed?
**Disclosure:** Are you planning to publicly disclose this issue?

## Additional Context
Any additional context that might help understand or fix the security issue.

---

## For Maintainers Only

### Response Checklist
- [ ] Issue severity confirmed
- [ ] Security team notified
- [ ] Fix timeline established
- [ ] Testing plan created
- [ ] Documentation updated
- [ ] Users notified (if appropriate)

### Fix Tracking
**Assigned to:** ___________
**Target Fix Date:** ___________
**Status:** 
- [ ] Investigating
- [ ] Fix in progress  
- [ ] Testing fix
- [ ] Ready to deploy
- [ ] Fixed and deployed
