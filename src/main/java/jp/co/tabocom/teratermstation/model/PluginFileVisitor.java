package jp.co.tabocom.teratermstation.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.yaml.snakeyaml.Yaml;

import jp.co.tabocom.teratermstation.plugin.TSPlugin;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;

public class PluginFileVisitor extends SimpleFileVisitor<Path> {

    private int depthCnt;
    private List<TeratermStationPlugin> nodePluginList;
    private List<Exception> loadExceptionList;

    public PluginFileVisitor(int depthCnt) {
        this.depthCnt = depthCnt;
        this.nodePluginList = new ArrayList<TeratermStationPlugin>();
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
                        InputStream yamlIs = loader.getResourceAsStream("plugin.yaml");
                        Yaml yaml = new Yaml();
                        Map<String, Object> pluginYaml = yaml.loadAs(yamlIs, Map.class);
                        TSPlugin tsPlugin = null;
                        // 基本情報
                        if (pluginYaml.containsKey("name") && pluginYaml.containsKey("version")) {
                            tsPlugin = new TSPlugin((String)pluginYaml.get("name"), (String)pluginYaml.get("version"));
                        } else {
                            throw new Exception();
                        }
                        if (pluginYaml.containsKey("class")) {
                            Map<String, Object> classMap = (Map<String, Object>)pluginYaml.get("class");
                            if (classMap.containsKey("context")) {
                                Map<String, Object> contextMap = (Map<String, Object>)classMap.get("context");
                                if (contextMap.containsKey("submenus") && contextMap.get("submenus") != null) {
                                    Class<?> cobj = loader.loadClass((String)contextMap.get("submenus"));
                                }
                                if (contextMap.containsKey("actions") && contextMap.get("actions") != null) {
                                    String actions = (String)contextMap.get("actions");
                                    for (String action : actions.split("\n")) {
                                        Class<?> cobj = loader.loadClass(action);
                                        Class superClass = cobj.getSuperclass();
                                        if (superClass == TeratermStationAction.class) {
                                            TeratermStationAction tsAction = (TeratermStationAction)cobj.newInstance();
                                            tsPlugin.addAction(tsAction);
                                        }
                                    }
                                }
                            }
                        } else {
                            throw new Exception();
                        }
//                        Class<?> cobj = loader.loadClass(cname);
//                        Class[] ifnames = cobj.getInterfaces();
//                        for (int j = 0; j < ifnames.length; j++) {
//                            if (ifnames[j] == TeratermStationPlugin.class) {
//                                System.out.println("load..... " + cname);
//                                TeratermStationPlugin plugin = (TeratermStationPlugin) cobj.newInstance();
//                                plugin.initialize();
//                                nodePluginList.add(plugin);
//                                break;
//                            }
//                        }
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

    public List<TeratermStationPlugin> getNodePluginList() {
        return nodePluginList;
    }

    public List<Exception> getLoadExceptionList() {
        return loadExceptionList;
    }
}
