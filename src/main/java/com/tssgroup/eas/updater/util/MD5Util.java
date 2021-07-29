package com.tssgroup.eas.updater.util;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

public class MD5Util {

    private static Properties md5 = null;
    public static String MD5_RESOURCE_FILENAME = "MD5.properties";
    public static String VERSION_RESOURCE_FILENAME = "version.properties";

    public MD5Util() {
    }

    public static void storeVersion(String localDir) {
        if (md5 != null) {
            Properties version = new Properties();
            String key = null;
            String value = null;
            Enumeration keys = md5.keys();

            while(keys.hasMoreElements()) {
                key = keys.nextElement().toString();
                value = md5.getProperty(key);
                if (value != null && value.indexOf(",") >= 0) {
                    value = value.substring(value.indexOf(",") + 1, value.length());
                    version.setProperty(key, value);
                }
            }

            File resourceFileHandler = new File(localDir, VERSION_RESOURCE_FILENAME);
            if (resourceFileHandler.exists()) {
                resourceFileHandler.delete();
            } else {
                try {
                    if (!resourceFileHandler.createNewFile()) {
                        System.out.println("create " + VERSION_RESOURCE_FILENAME + " error.");
                    }
                } catch (IOException var23) {
                    System.out.println("create " + VERSION_RESOURCE_FILENAME + " error.");
                    var23.printStackTrace();
                }
            }

            BufferedOutputStream bs = null;

            try {
                bs = new BufferedOutputStream(new FileOutputStream(resourceFileHandler));
                version.store(bs, (String)null);
            } catch (FileNotFoundException var20) {
                System.out.println("store " + VERSION_RESOURCE_FILENAME + " error.");
                var20.printStackTrace();
            } catch (IOException var21) {
                System.out.println("store " + VERSION_RESOURCE_FILENAME + " error.");
                var21.printStackTrace();
            } finally {
                try {
                    if (bs != null) {
                        bs.close();
                    }
                } catch (IOException var19) {
                    System.out.println("IOException when closing!");
                }

            }
        }

    }

    public static void storeMD5(String localDir) {
        if (md5 != null) {
            File resourceFileHandler = new File(localDir, MD5_RESOURCE_FILENAME);
            if (resourceFileHandler.exists()) {
                resourceFileHandler.delete();
            } else {
                try {
                    if (!resourceFileHandler.createNewFile()) {
                        System.out.println("create " + MD5_RESOURCE_FILENAME + " error.");
                    }
                } catch (IOException var16) {
                    System.out.println("create " + MD5_RESOURCE_FILENAME + " error.");
                    var16.printStackTrace();
                }
            }

            BufferedOutputStream bs = null;

            try {
                bs = new BufferedOutputStream(new FileOutputStream(resourceFileHandler));
                md5.store(bs, (String)null);
            } catch (Exception var14) {
                System.out.println("store " + MD5_RESOURCE_FILENAME + " error.");
                resourceFileHandler.delete();
                var14.printStackTrace();
            } finally {
                try {
                    if (bs != null) {
                        bs.close();
                    }
                } catch (IOException var13) {
                    System.out.println("IOException when closing!");
                }

            }
        }

    }

    public static void loadMD5(String localDir) {
        File resourceFileHandler = new File(localDir, MD5_RESOURCE_FILENAME);
        if (!resourceFileHandler.exists()) {
            try {
                if (!resourceFileHandler.createNewFile()) {
                    System.out.println("create " + MD5_RESOURCE_FILENAME + " error.");
                }
            } catch (IOException var16) {
                System.out.println("create " + MD5_RESOURCE_FILENAME + " error.");
                var16.printStackTrace();
            }
        }

        if (resourceFileHandler.exists()) {
            BufferedInputStream bs = null;

            try {
                bs = new BufferedInputStream(new FileInputStream(resourceFileHandler));
                md5 = new Properties();
                md5.load(bs);
            } catch (Exception var14) {
                System.out.println("load " + MD5_RESOURCE_FILENAME + " error.");
                resourceFileHandler.delete();
                var14.printStackTrace();
            } finally {
                try {
                    if (bs != null) {
                        bs.close();
                    }
                } catch (IOException var13) {
                    System.out.println("IOException when closing!");
                }

            }
        }

    }

    public static Properties getMD5(String localDir) {
        if (md5 == null) {
            loadMD5(localDir);
        }

        return md5;
    }

    public static long getLastModified(Properties pro, String key) {
        long l = 0L;
        if (pro != null && key != null) {
            String value = pro.getProperty(key);
            if (value != null && value.indexOf(",") >= 0) {
                value = value.substring(0, value.indexOf(","));

                try {
                    l = Long.parseLong(value);
                } catch (NumberFormatException var6) {
                    l = 0L;
                    var6.printStackTrace();
                }
            }

            return l;
        } else {
            return l;
        }
    }

    public static String getMD5Version(Properties pro, String key) {
        String value = null;
        if (pro != null && key != null) {
            value = pro.getProperty(key);
            if (value != null && value.indexOf(",") >= 0) {
                value = value.substring(value.indexOf(",") + 1);
            } else {
                value = null;
            }

            return value;
        } else {
            return value;
        }
    }

    public static void main(String[] sr) {
        String s = "/metas/fi_fa_facade-metas.jar";
        s = s.indexOf("/") == 0 ? s.substring(1) : s;
        System.out.println(s);
    }
}
