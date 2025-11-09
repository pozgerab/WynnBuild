package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.webquery.providers.AspectProvider;
import com.gertoxq.wynnbuild.webquery.providers.ItemProvider;
import com.gertoxq.wynnbuild.webquery.providers.MidProvider;
import com.gertoxq.wynnbuild.webquery.providers.TomeProvider;

public class Providers {

    public static final AspectProvider Aspects = new AspectProvider();
    public static final ItemProvider Items = new ItemProvider();
    public static final MidProvider MajorIds = new MidProvider();
    public static final TomeProvider Tomes = new TomeProvider();

}
