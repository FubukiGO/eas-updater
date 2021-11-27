package com.tssgroup.eas.updater.util;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PackageMapGenerator {
    public static String PKMAP_RESOURCE_FILENAME = "pkCache.lst";
    private static boolean modified = false;
    private static Map oldMap = null;
    private static Map newMap = null;
    private static Vector packageNames = null;
    private static String cachePath = null;

    public PackageMapGenerator() {
        super();
    }

    public static void generatePKMap(String home, String libPath, String outputpath) {
        System.out.println("--generatePKMap--path" + home);
    }

    private static Map getOldPKMap() {
        if (oldMap == null) {
            oldMap = loadPKMap();
        }

        return oldMap;
    }

    private static Map getNewPKMap() {
        if (newMap == null) {
            newMap = new HashMap();
        }

        return newMap;
    }

    private static Vector getPackageNames() {
        if (packageNames == null) {
            packageNames = new Vector();
        }

        return packageNames;
    }

    public static Map loadPKMap() {
        long startTime = System.currentTimeMillis();
        Map map = null;
        ObjectInputStream objectinputstream = null;
        File file = new File(cachePath, PKMAP_RESOURCE_FILENAME);
        if (file.exists()) {
            try {
                map = (Map)(objectinputstream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))).readObject();
            } catch (Exception var15) {
                file.delete();
                map = new HashMap();
                var15.printStackTrace();
            } finally {
                if (objectinputstream != null) {
                    try {
                        objectinputstream.close();
                    } catch (IOException var14) {
                        var14.printStackTrace();
                    }
                }

            }
        }

        if (map == null) {
            map = new HashMap();
        }

        System.out.println("[easupdater] load pkCache resource. used time:" + (System.currentTimeMillis() - startTime) + "ms");
        return (Map)map;
    }

    public static void storePKMap() {
        if (newMap != null) {
            long startTime = System.currentTimeMillis();
            ObjectOutputStream objectoutputstream = null;
            File file = new File(cachePath, PKMAP_RESOURCE_FILENAME);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            } else {
                file.delete();
            }

            try {
                (objectoutputstream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))).writeObject(newMap);
            } catch (Exception var15) {
                file.delete();
                var15.printStackTrace();
            } finally {
                if (objectoutputstream != null) {
                    try {
                        objectoutputstream.close();
                    } catch (IOException var14) {
                        var14.printStackTrace();
                    }
                }

            }

            System.out.println("[easupdater] store pkCache resource. used time:" + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    private static void storeMap(File file, Map map) {
        try {
            FileWriter pkMapWriter = new FileWriter(file);
            BufferedWriter bufPKMapWriter = new BufferedWriter(pkMapWriter);
            String[] keys = (String[])map.keySet().toArray(new String[0]);
            Arrays.sort(keys);

            for(int i = 0; i < keys.length; ++i) {
                bufPKMapWriter.write(keys[i]);
                bufPKMapWriter.write("=");
                bufPKMapWriter.write(map.get(keys[i]).toString());
                bufPKMapWriter.write("\r\n");
            }

            bufPKMapWriter.flush();
            pkMapWriter.close();
        } catch (Throwable var6) {
            System.err.println("[easupdater] store pkMap failed, cause by " + var6.getMessage());
            var6.printStackTrace();
        }

    }

    public static boolean generatePKMap(String home, String libPath, String outputpath, String fileName, String configPath) {
        cachePath = configPath;
        oldMap = null;
        newMap = null;
        packageNames = null;
        Properties md5 = MD5Util.getMD5(configPath);
        modified = false;
        File resHome = new File(home);
        File file = null;
        Properties resList = new Properties();

        try {
            String homePath = resHome.getCanonicalPath();
            String[] libList = libPath.split(",");

            for(int i = 0; i < libList.length; ++i) {
                System.out.println("----liblist" + i + "=" + libList[i]);
                file = new File(resHome, libList[i]);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        scanDir(homePath.length(), resHome, libList[i], md5);
                    } else {
                        scanFile(file, homePath.length(), md5);
                    }
                }
            }

            if (getOldPKMap().size() != getNewPKMap().size()) {
                modified = true;
            }

            if (modified) {
                String key = null;
                Object object = null;
                Vector vector = null;
                int size = getPackageNames().size();

                for(int k = 0; k < size; ++k) {
                    key = getPackageNames().get(k).toString();
                    object = getNewPKMap().get(key);
                    if (object != null && object instanceof Vector) {
                        vector = (Vector)object;
                        int num = vector.size();

                        for(int j = 0; j < num; ++j) {
                            addPackageToList((String)vector.get(j), "/" + key, resList);
                        }
                    }
                }

                storeMap(new File(outputpath + "/" + fileName), resList);
                storePKMap();
            } else {
                System.out.println("[easupdater] pkmap resource no modified.");
            }
        } catch (Exception var18) {
            var18.printStackTrace();
        }

        return modified;
    }

    private static void scanDir(int homeLength, File parent, String sub, Properties md5) {
        File path = new File(parent, sub);
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null && files.length != 0) {
                File file = null;

                for(int i = 0; i < files.length; ++i) {
                    file = files[i];
                    if (file.isDirectory()) {
                        scanDir(homeLength, path, file.getName(), md5);
                    } else {
                        scanFile(file, homeLength, md5);
                    }
                }

            }
        }
    }

    private static void scanFile(File file, int homeLength, Properties md5) {
        try {
            String jarPath = null;
            String key = null;
            Vector vector = null;
            Object object = null;
            String filename = file.getCanonicalPath();
            if (filename.toLowerCase().endsWith(".jar") || filename.toLowerCase().endsWith(".zip") || filename.toLowerCase().endsWith(".rar")) {
                filename = filename.replace('\\', '/');
                jarPath = filename.substring(homeLength);
                key = jarPath.indexOf("/") == 0 ? jarPath.substring(1) : jarPath;
                if (file.lastModified() != MD5Util.getLastModified(md5, key)) {
                    vector = scanPackage(file, jarPath);
                    getNewPKMap().put(key, vector);
                    getPackageNames().add(key);
                    modified = true;
                } else {
                    object = getOldPKMap().get(key);
                    if (object != null && object instanceof Vector) {
                        getNewPKMap().put(key, object);
                        getPackageNames().add(key);
                    } else {
                        vector = scanPackage(file, jarPath);
                        getNewPKMap().put(key, vector);
                        getPackageNames().add(key);
                        modified = true;
                    }
                }
            }
        } catch (IOException var8) {
            var8.printStackTrace();
        }

    }

    private static Vector scanPackage(File file, String jarPath) {
        Vector vector = new Vector();
        String packageName = "";

        try {
            ZipFile lib = new ZipFile(file);
            Enumeration en = lib.entries();

            while(en.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)en.nextElement();
                if (!entry.isDirectory()) {
                    packageName = entry.getName();
                    if (packageName.lastIndexOf("/") >= 0) {
                        packageName = packageName.substring(0, packageName.lastIndexOf(47));
                    }

                    if (!vector.contains(packageName)) {
                        vector.add(packageName);
                    }
                }
            }

            lib.close();
        } catch (Exception var7) {
            if (file.getName().endsWith("metasindex.jar")) {
                vector.add("index.lst");
            }

            file.delete();
            modified = true;
            var7.printStackTrace();
        }

        return vector;
    }

    private static void addPackageToList(String packageName, String jarPath, Properties resList) {
        String jarList = resList.getProperty(packageName, "");
        if (jarList.length() > 0) {
            String[] packageList = jarList.split(";");
            List pkList = Arrays.asList(packageList);
            if (!pkList.contains(jarPath)) {
                resList.setProperty(packageName, jarList + ";" + jarPath);
            }
        } else {
            resList.setProperty(packageName, jarPath);
        }

    }
}
