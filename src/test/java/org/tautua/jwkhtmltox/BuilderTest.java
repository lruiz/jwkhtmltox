package org.tautua.jwkhtmltox;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BuilderTest {

    @Test
    public void buildPdf() throws FileNotFoundException {
        InputStream input = new FileInputStream("src/test/resources/helloworld.html");
        OutputStream output = new FileOutputStream("target/document.pdf");
        new PdfBuilder()
                .fromInput(input)
                .toOutput(output)
                .build();
    }

    @Test
    public void buildPdfComplex() throws FileNotFoundException {
        InputStream input = new FileInputStream("src/test/resources/test.html");
        OutputStream output = new FileOutputStream("target/document.pdf");
        new PdfBuilder()
                .fromInput(input)
                .toOutput(output)
                .build();
    }

    @Test
    public void buildImage() throws IOException {
        InputStream input = new ByteArrayInputStream("<html><body><p>Hello World<p></body></html>".getBytes(StandardCharsets.UTF_8));
        OutputStream output = new FileOutputStream(new File("target/image.jpeg"));
        new ImageBuilder()
                .fromInput(input)
                .toOutput(output)
                .build();
        output.close();
    }

    @Test
    public void buildImageFromHtmlToJpg() throws IOException {
        InputStream input = new FileInputStream("src/test/resources/test.html");
        OutputStream output = new FileOutputStream("target/image.jpeg");
        new ImageBuilder()
                .fromInput(input)
                .toOutput(output)
                .withDimensions(650, 570)
                .build();

        output.close();
    }

    @Test
    public void buildImageFromHtmlToPng() throws IOException {
        InputStream input = new FileInputStream("src/test/resources/test.html");
        OutputStream output = new FileOutputStream("target/image.png");
        new ImageBuilder()
                .fromInput(input)
                .toOutput(output)
                .withFormat(ImageFormat.PNG)
                .withQuality(.1)
                .withDimensions(650, 570)
                .build();

        output.close();
    }
}
