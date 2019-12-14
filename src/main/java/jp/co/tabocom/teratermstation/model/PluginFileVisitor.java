/*
 * MIT License
 * Copyright (c) 2015-2019 Tabocom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;

public class PluginFileVisitor extends SimpleFileVisitor<Path> {

    private int depthCnt;
    private List<TeratermStationPlugin> pluginList;
    private List<Exception> loadExceptionList;

    public PluginFileVisitor(int depthCnt) {
        this.depthCnt = depthCnt;
        this.pluginList = new ArrayList<TeratermStationPlugin>();
        this.loadExceptionList = new ArrayList<Exception>();
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
                            if (ifnames[j] == TeratermStationPlugin.class) {
                                System.out.println("load..... " + cname);
                                TeratermStationPlugin plugin = (TeratermStationPlugin) cobj.newInstance();
                                try {
                                    plugin.getClass().getDeclaredMethod("initialize");
                                } catch (NoSuchMethodException | SecurityException e) {
                                    continue;
                                }
                                plugin.initialize();
                                pluginList.add(plugin);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadExceptionList.add(new Exception(String.format("%s のロードに失敗しました。", file.getName())));
                    } finally {
                        jar.close();
                    }
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

    public List<TeratermStationPlugin> getPluginList() {
        return pluginList;
    }

    public List<Exception> getLoadExceptionList() {
        return loadExceptionList;
    }
}
