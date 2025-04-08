package org.fentanylsolutions.tabfaces.varinstances;

import java.io.File;

import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.registries.ServerRegistry;

public class VarInstanceServer {

    public File skinCachePath = new File(
        TabFaces.rootPath,
        "cache" + File.separator + "client" + File.separator + "skins");
    public ServerRegistry serverRegistry = new ServerRegistry();
}
