package me.shib.tools.dns.server;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

final class AddressCache {

    private static final int resetDuration = 30000;

    private int pos;
    private List<Addr> addrs;
    private long lastRequestTime;

    AddressCache(List<Addr> addrs) {
        this.pos = 0;
        this.addrs = addrs;
        this.lastRequestTime = 0;
    }

    synchronized Addr getAddr() {
        if (addrs != null && addrs.size() > 0) {
            long currentTime = new Date().getTime();
            if ((currentTime - lastRequestTime) > resetDuration) {
                pos = 0;
            } else {
                pos = pos % addrs.size();
            }
            lastRequestTime = currentTime;
            Addr addr = addrs.get(pos);
            pos++;
            try {
                Thread.sleep(addr.getSleepInMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return addr;
        }
        return null;
    }

    static class Addr {
        private InetAddress address;
        private int sleepInMillis;

        Addr(InetAddress address, int sleepInMillis) {
            this.address = address;
            this.sleepInMillis = sleepInMillis;
        }

        InetAddress getAddress() {
            return address;
        }

        int getSleepInMillis() {
            return sleepInMillis;
        }
    }

}
