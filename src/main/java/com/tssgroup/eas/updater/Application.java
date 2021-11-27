package com.tssgroup.eas.updater;

import com.tssgroup.eas.updater.util.PackageMapGenerator;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入eas根目录: ");
        String rootPath = sc.next();
        String home = rootPath + "/eas/server/deploy/fileserver.ear/easWebClient";
        String libPath = "lib";
        PackageMapGenerator.generatePKMap(home, libPath, home+"/classloader", "pkmap.lst", rootPath + "/eas/server/properties/");
        System.out.println("Generate package map list: " + (System.currentTimeMillis() - startTime));
    }
}
