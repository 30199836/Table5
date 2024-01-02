package com.searesoft.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Quick and dirty to way to read a registry key
 */
public class Registry {
    public static final String readString(String location, String key) {
        try {
            // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec("reg query " +
                    '"' + location + "\" /v " + key);

            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();

            String s1[];
            try {
                s1 = reader.getResult().split("REG_SZ|REG_DWORD|REG_EXPAND_SZ");
            } catch (Exception e) {
                return null;
            }
            return s1[1].trim();
        } catch (Exception e) {
        }

        return null;
    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw = new StringWriter();

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    //System.out.println(c);
                    sw.write(c);
            } catch (IOException e) {
            }
        }

        public String getResult() {
            return sw.toString();
        }
    }
}
