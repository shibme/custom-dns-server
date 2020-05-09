### Custom DNS Server
Custom DNS Server for Security Assessments

### How to Run
* Point the `NS` records of your domain to the server's IP address.
* Stop any services that are listening in port `53`
* Pull the docker image
```bash
docker pull shibme/custom-dns-server
```
* Run the following with root (sudo) privilege
```bash
docker run -p 53:53 --name cdns --restart always -d shibme/custom-dns-server
```