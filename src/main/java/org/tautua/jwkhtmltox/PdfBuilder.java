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
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tautua.jwkhtmltox.jna.WebKitHtmlToPdfLibrary;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PdfBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageBuilder.class);

    private InputStream input;
    private OutputStream output;

    public PdfBuilder fromInput(InputStream input){
        this.input = input;
        return this;
    }

    public PdfBuilder toOutput(OutputStream output){
        this.output = output;
        return this;
    }

    public void build() {
        WebKitHtmlToPdfLibrary library = WebKitHtmlToPdfLibrary.INSTANCE;
        library.wkhtmltopdf_init(0);

        PointerByReference globals = library.wkhtmltopdf_create_global_settings();
//        library.wkhtmltopdf_set_global_setting(globals, "out", "target/mydoc.pdf");
        // set settings
        PointerByReference converter = library.wkhtmltopdf_create_converter(globals);

        library.wkhtmltopdf_set_warning_callback(converter, (c, s) -> LOGGER.warn(s));
        library.wkhtmltopdf_set_error_callback(converter, (c,s) -> LOGGER.error(s));

        try {
            PointerByReference settings = library.wkhtmltopdf_create_object_settings();

            library.wkhtmltopdf_add_object(converter, settings, readData(input));

            if (library.wkhtmltopdf_convert(converter) == 1) {
                PointerByReference out = new PointerByReference();
                NativeLong size = library.wkhtmltopdf_get_output(converter, out);
                byte[] rawbytes = new byte[size.intValue()];
                out.getValue().read(0, rawbytes, 0, rawbytes.length);
                output.write(rawbytes);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            library.wkhtmltopdf_destroy_converter(converter);
        }
    }

    private String readData(InputStream input) throws IOException {
        StringBuilder buff = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(input, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                buff.append((char) c);
            }
        }
        return buff.toString();
    }
}
