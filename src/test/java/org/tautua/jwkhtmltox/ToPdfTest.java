package org.tautua.jwkhtmltox;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ToPdfTest {

    @Test
    public void buildPdf() throws FileNotFoundException {
        InputStream input = new FileInputStream("src/test/resources/helloworld.html");
        OutputStream output = new FileOutputStream("target/helloworld.pdf");
        new PdfBuilder()
                .fromInput(input)
                .toOutput(output)
                .build();
    }
}
