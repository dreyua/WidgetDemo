package com.example.drey.widgetdemo;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

/**
 * Created by drey on 20.01.2016.
 */
public class Utils {
    public static boolean isOpenOrPSK(Context context) {
        boolean res = false;
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> networkList = wifi.getScanResults();
        WifiInfo wi = wifi.getConnectionInfo();
        String currentSSID = trimQuotes(wi.getSSID()); //returns value surrounded with quotes on my phone

        if (networkList != null) {
            for (ScanResult network : networkList) {
                if (currentSSID.equals(trimQuotes(network.SSID))) {
                    String cap = network.capabilities.toUpperCase();
                    // Log.d("WIFI TEST", network.SSID + " capabilities : " + cap);
                    if (cap.contains("PSK")) {
                        res = true;
                    } else if (!cap.contains("WEP") && !cap.contains("EAP")) {
                        res = true;
                    }
                }
            }
        }
        return res;

    }

    public static boolean pingGoogle() {
        boolean res = false;
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName("www.google.com");
            SocketAddress sockaddr = new InetSocketAddress(addr, 80);
            Socket sock = new Socket();
            int timeoutMs = 300;
            sock.connect(sockaddr, timeoutMs);
            res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String trimQuotes(String text) {
        if (text.startsWith("\"") && text.endsWith("\"")) {
            return text.substring(1, text.length() - 1);
        } else {
            return text;
        }
    }
}
