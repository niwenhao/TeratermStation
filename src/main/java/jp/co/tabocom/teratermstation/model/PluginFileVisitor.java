package jp.co.tabocom.teratermstation.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import jp.co.tabocom.teratermstation.plugin.TeraTermStationPlugin;

public class PluginFileVisitor extends SimpleFileVisitor<Path> {

    private int depthCnt;
    private List<TeraTermStationPlugin> nodePluginList;

    public PluginFileVisitor(int depthCnt) {
        this.depthCnt = depthCnt;
        this.nodePluginList = new ArrayList<TeraTermStationPlugin>();
    }

    @Override
    public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
        return super.postVisitDirectory(arg0, arg1);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dirPath, BasicFileAttributes attr) throws IOException {
        return super.preVisitDirectory(dirPath, attr);
    }

    @Override
    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attr) throws IOException {
        String fileName = filePath.getFileName().toString();
        File file = filePath.toFile();
        int depth = filePath.getNameCount() - this.depthCnt;
        String parentDir = file.getParentFile().getName();
        switch (depth) {
            case 3: {
                // ========== *.JAR ========== //
                if (parentDir.equals("plugins") && fileName.endsWith(".jar")) {
                    JarFile jar = new JarFile(file);
                    Manifest mf = jar.getManifest();
                    Attributes att = mf.getMainAttributes();
                    String cname = att.getValue("Plugin-Class");
                    URL url = file.getCanonicalFile().toURI().toURL();
                    URLClassLoader loader = new URLClassLoader(new URL[] { url });
                    try {
                        Class<?> cobj = loader.loadClass(cname);
                        Class[] ifnames = cobj.getInterfaces();
                        for (int j = 0; j < ifnames.length; j++) {
                            if (ifnames[j] == TeraTermStationPlugin.class) {
                                System.out.println("load..... " + cname);
                                TeraTermStationPlugin plugin = (TeraTermStationPlugin) cobj.newInstance();
                                nodePluginList.add(plugin);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jar.close();
                }
                break;
            }
            default:
        }
        // System.out.format("File: %s - %d\n", arg0, arg0.getNameCount());
        return super.visitFile(filePath, attr);
    }

    @Override
    public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
        return super.visitFileFailed(arg0, arg1);
    }

    public List<TeraTermStationPlugin> getNodePluginList() {
        return nodePluginList;
    }
}
