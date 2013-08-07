package me.hanqin.apps.inspect;

import android.app.Instrumentation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import me.hanqin.apps.inspect.handlers.BaseHandler;
import me.hanqin.apps.inspect.handlers.DatabaseHandler;
import me.hanqin.apps.inspect.server.DefaultRequestDispatcher;
import me.hanqin.apps.inspect.server.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Guide for using this tool to setup the data server url
 * 1, build HHT5_Core, install
 * ant debug; ant installd
 * 2, build HHT5_Config (this project), install
 * ant debug; ant installd
 * 3, adb shell am instrument me.hanqin.apps.inspect/me.hanqin.apps.inspect.Inspectable
 * Please take a look at the adb logcat output, you should find something like :
 * <code>inetAddress.getHostAddress() = 192.168.56.101</code>
 *
 * Now, open you browser and try following urls:
 * To browse all databases:     GET http://your-address:10086/database/
 * To inspect a database:       GET http://your-address:10086/database/db_name.db
 * To manipulate a database:    POST http://your-address:10086/database/db_name.db
 *                                   with params set to => sql: your-sql-script
 *                                   with optional param set to => bulk: true if it bulk operations, separated by ;
 *
 * <p/>
 */
public class Inspectable extends Instrumentation {

    public static final String KEY_HTTP_PORT = "port";

    @Override
    public void onCreate(Bundle arguments) {
        int port = arguments.getInt(KEY_HTTP_PORT, 10086);
        HttpServer.instantiate(port);
        try {
            HttpServer httpServer = HttpServer.getInstance();
            httpServer.setRequestDispatcher(defaultDispatcher());
            httpServer.start();
            printNetworkAddress(getTargetContext());
            System.out.println("Server started, listening on port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DefaultRequestDispatcher defaultDispatcher() {
        DefaultRequestDispatcher requestDispatcher = new DefaultRequestDispatcher();
        HashMap<String, BaseHandler> handlers = new HashMap<String, BaseHandler>();
        handlers.put(BaseHandler.DISPATCH_KEY_DATABASE, new DatabaseHandler(getTargetContext()));
        requestDispatcher.setHandlers(handlers);
        return requestDispatcher;
    }

    private static void printNetworkAddress(Context context) throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

            System.out.println("networkInterface = " + networkInterface.getName());
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress.isLoopbackAddress()) continue;
                System.out.println("inetAddress.getHostAddress() = " + inetAddress.getHostAddress());
            }
        }
    }

    private static boolean printWifiWhenConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !"".equalsIgnoreCase(connectionInfo.getSSID())) {
                int ip = connectionInfo.getIpAddress();
                String ipString = String.format(
                        "%d.%d.%d.%d",
                        (ip & 0xff),
                        (ip >> 8 & 0xff),
                        (ip >> 16 & 0xff),
                        (ip >> 24 & 0xff));
                System.out.println("ipString = " + ipString);
                return true;
            }
        }
        return false;
    }
}
