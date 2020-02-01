package me.shib.tools.dns.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public final class LaunchDNS {
    public static void main(String[] args) {
        DnsServer server = new DnsServer();
        List<AddressCache.Addr> addrs = new ArrayList<>();
        try {
            addrs.add(new AddressCache.Addr(InetAddress.getByName("1.1.1.1"), 0));
            addrs.add(new AddressCache.Addr(InetAddress.getByName("169.254.169.254"), 0));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        server.setDefaultAddr(addrs);
        server.start(53);
    }
}
