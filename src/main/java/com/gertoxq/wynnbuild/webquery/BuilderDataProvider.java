package com.gertoxq.wynnbuild.webquery;

import java.lang.reflect.Type;
import java.util.Map;

import static com.gertoxq.wynnbuild.webquery.BuilderDataManager.LATEST_WYNNBUILDER_VERSION;

public abstract class BuilderDataProvider<V> extends DataProvider<Map<String, V>> {

    private final String dataFolderUrl = "https://raw.githubusercontent.com/wynnbuilder/wynnbuilder.github.io/refs/heads/master/data/";

    public BuilderDataProvider(String name, Type dataType) {
        super(name, dataType);
    }

    @Override
    public String url() {
        return dataFolderUrl + LATEST_WYNNBUILDER_VERSION + "/" + name + ".json";
    }
}
