package com.gertoxq.wynnbuild;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WynnBuildTest {

    @Test
    public void onBuild_notlocalhost() {
        WynnBuild.info("DOMAIN = {}", WynnBuild.DOMAIN);
        assertEquals(WynnBuild.DOMAIN, "https://wynnbuilder.github.io/");
    }

}