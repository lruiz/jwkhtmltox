package org.tautua.jwkhtmltox;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.PointerByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tautua.jwkhtmltox.jna.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ImageBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageBuilder.class);
    private InputStream input;
    private OutputStream output;
    private ImageFormat format = ImageFormat.JPG;
    private Integer width;
    private Integer height;
    private Charset encoding = StandardCharsets.UTF_8;

    public ImageBuilder fromInput(InputStream input){
        this.input = input;
        return this;
    }

    public ImageBuilder toOutput(OutputStream output){
        this.output = output;
        return this;
    }

    public ImageBuilder withFormat(ImageFormat format){
        this.format = format;
        return this;
    }

    public ImageBuilder withDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public void build() {
        WebKitHtmlToImageLibrary library = WebKitHtmlToImageLibrary.INSTANCE;
        library.wkhtmltoimage_init(0);

        PointerByReference globals = library.wkhtmltoimage_create_global_settings();
        library.wkhtmltoimage_set_global_setting(globals, "fmt", format.name().toLowerCase(Locale.ROOT));
        if(width != null) {
            library.wkhtmltoimage_set_global_setting(globals, "screenWidth", width.toString());
            library.wkhtmltoimage_set_global_setting(globals, "smartWidth", "false");
        }
        if(height != null) {
            library.wkhtmltoimage_set_global_setting(globals, "screenHeight", height.toString());
            library.wkhtmltoimage_set_global_setting(globals, "smartWidth", "false");
        }

        library.wkhtmltoimage_set_global_setting(globals, "web.defaultEncoding", encoding.name().toLowerCase());
        PointerByReference converter = null;
        try {
            converter = library.wkhtmltoimage_create_converter(globals, readData(input));
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }

        library.wkhtmltoimage_set_warning_callback(converter, (c, s) -> LOGGER.warn(s));
        library.wkhtmltoimage_set_error_callback(converter, (c,s) -> LOGGER.error(s));

        try {
            if (library.wkhtmltoimage_convert(converter) == 1) {
                PointerByReference out = new PointerByReference();
                NativeLong size = library.wkhtmltoimage_get_output(converter, out);
                byte[] rawbytes = new byte[size.intValue()];
                out.getValue().read(0, rawbytes, 0, rawbytes.length);
                output.write(rawbytes);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            library.wkhtmltoimage_destroy_converter(converter);
        }
    }

    private String readData(InputStream input) throws IOException {
        StringBuilder buff = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(input, encoding))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                buff.append((char) c);
            }
        }
        return buff.toString();
    }
}
