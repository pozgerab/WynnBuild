package com.gertoxq.wynnbuild.base.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BootstringEncoderTest {

    @Test
    public void BootstringEncoder_shouldEncodeAndDecodeTheSame() {
        String input = "An auxiliary power core of the powerful TERA-4M Mining robot line. It controls the robots by giving the bearer a compulsion to break new grounds and progress further without heed.";
        BootstringEncoder encoder = new BootstringEncoder(0, 1, 52, 104, 700, 38, '-');
        String encoded = encoder.encode(input);
        String decoded = encoder.decode(encoded);

        assertEquals(input, decoded);
        assertEquals("AnauxiliarypowercoreofthepowerfulTERA-4MMiningrobotlineItcontrolstherobotsbygivingthebeareracompulsiontobreaknewgroundsandprogressfurtherwithoutheed-Ca19542387654283626361A25373877srIq6", encoded);
    }

}