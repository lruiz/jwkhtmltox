/*
 * Copyright 2021, TAUTUA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.String.valueOf;

public class ImageBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageBuilder.class);
    private InputStream input;
    private OutputStream output;
    private ImageFormat format = ImageFormat.JPG;
    private Integer width;
    private Integer height;
    private Charset encoding = StandardCharsets.UTF_8;
    private Double quality = 0.94;

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

    public ImageBuilder withQuality(double quality) {
        this.quality = quality;
        return this;
    }

    public void build() {
        Future f = Engine.getInstance().doWork(() -> {
            LOGGER.debug("working");
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

            if(quality != null) {
                library.wkhtmltoimage_set_global_setting(globals, "quality", valueOf((int)(quality * 100)));
            }

            library.wkhtmltoimage_set_global_setting(globals, "web.defaultEncoding", encoding.name().toLowerCase());
            final PointerByReference converter;
            try {
                converter = library.wkhtmltoimage_create_converter(globals, readData(input));
            } catch(IOException e) {
                throw new IllegalStateException(e);
            }

            library.wkhtmltoimage_set_warning_callback(converter, (c, s) -> LOGGER.warn(s));
            library.wkhtmltoimage_set_error_callback(converter, (c,s) -> LOGGER.error(s));
            library.wkhtmltoimage_set_progress_changed_callback(converter, (c, phaseProgress) -> {
                int current = library.wkhtmltoimage_current_phase(converter);
                int total = library.wkhtmltoimage_phase_count(converter);
                String description = library.wkhtmltoimage_phase_description(converter, current);
                LOGGER.debug("{}/{}, phase {}", current, total, description);
            });
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
//                library.wkhtmltoimage_deinit();
            }
            LOGGER.debug("work finished");
        });
        try {
            f.get();
        } catch(InterruptedException e) {
            e.printStackTrace();
        } catch(ExecutionException e) {
            e.printStackTrace();
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
