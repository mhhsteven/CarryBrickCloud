package org.mao.utils;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Set;

/**
 * 获取本机ip列表工具
 *
 * @author mhh
 */
public class IpAddressUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpAddressUtils.class);

    private static Set<String> ipAddresses = Sets.newHashSet();

    public static boolean isLocal(String ip) {
        if (CollectionUtils.isEmpty(ipAddresses)) {
            init();
        }
        return ipAddresses.contains(ip);
    }

    public static void init() {
        Enumeration allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException se) {
            LOGGER.error("获取本机ip列表失败", se);
        }
        if (allNetInterfaces != null) {
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        ipAddresses.add(ip.getHostAddress());
                    }
                }
            }
        }
    }
}
