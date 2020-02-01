package me.shib.tools.dns.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

public class DnsServer {
    private static final Logger LOG = LoggerFactory.getLogger("ShibMe DNS Server");
    private static final int UDP_SIZE = 512;
    private static final long TTL = 1;

    private final Map<String, AddressCache> aRecords = synchronizedMap(new HashMap<>());

    private List<AddressCache.Addr> defaultAddrs;

    void setDefaultAddr(List<AddressCache.Addr> defaultAddrs) {
        this.defaultAddrs = defaultAddrs;
    }

    void addARecords(String domain, List<AddressCache.Addr> addrs) {
        AddressCache addressCache = new AddressCache(addrs);
        aRecords.put(domain.replaceAll("\\.$", ""), addressCache);
    }

    void start(int port) {
        Thread thread = new Thread(() -> serveUDP(port));
        thread.setName("DNS server");
        thread.start();
        LOG.info("Listening in port {}", port);
    }

    private void serveUDP(int port) {
        try (DatagramSocket sock = new DatagramSocket(port)) {
            while (true) {
                process(sock);
            }
        } catch (IOException e) {
            LOG.error("Failed to create socket", e);
        }
    }

    private void process(DatagramSocket sock) {
        try {
            byte[] in = new byte[UDP_SIZE];
            DatagramPacket indp = new DatagramPacket(in, UDP_SIZE);
            indp.setLength(UDP_SIZE);
            sock.receive(indp);
            Message msg = new Message(in);
            Header header = msg.getHeader();
            Record question = msg.getQuestion();
            Message response = new Message(header.getID());
            response.getHeader().setFlag(Flags.QR);
            response.addRecord(question, Section.QUESTION);
            Name name = question.getName();
            boolean found = false;
            if (question.getType() == Type.A) {
                AddressCache addressCache = aRecords.get(name.toString(true));
                if (addressCache == null && defaultAddrs != null) {
                    addressCache = new AddressCache(defaultAddrs);
                    aRecords.put(name.toString(true), addressCache);
                }
                AddressCache.Addr aRecord = null;
                if (addressCache != null) {
                    aRecord = addressCache.getAddr();
                }
                if (aRecord != null) {
                    response.addRecord(new ARecord(name, DClass.IN, TTL, aRecord.getAddress()), Section.ANSWER);
                    found = true;
                    LOG.info("DNS: {}: {} {} IN A {}", new Date(), name, TTL, aRecord.getAddress().getHostAddress());
                }
            }
            if (!found) {
                LOG.warn("DNS: Not found: {}: {}", new Date(), question);
            }
            byte[] resp = response.toWire();
            DatagramPacket out = new DatagramPacket(resp, resp.length, indp.getAddress(), indp.getPort());
            sock.send(out);
        } catch (Exception e) {
            LOG.error("Query was not processed", e);
        }
    }

}