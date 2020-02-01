### Custom DNS Server
Custom DNS Server for Security Assessments

### How to Run
Point the `NS` records of your domain to the server's IP address and then execute the following in your terminal (requires root).
Also don't forget to stop any services listening in port `53`
```bash
curl -s https://shibme.github.io/custom-dns-server/launcher | bash
```