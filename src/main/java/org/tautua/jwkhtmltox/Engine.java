/*
 * Copyright 2021 TAUTUA
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tautua.jwkhtmltox.jna.WebKitHtmlToImageLibrary;
import org.tautua.jwkhtmltox.jna.WebKitHtmlToPdfLibrary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Engine {
    private static final Logger LOGGER = LoggerFactory.getLogger(Engine.class);
    private static final Engine INSTANCE = new Engine();
    private final ExecutorService executorService;
    private Status status = Status.INACTIVE;
    private WebKitHtmlToImageLibrary imageLibrary = WebKitHtmlToImageLibrary.INSTANCE;
    private WebKitHtmlToPdfLibrary pdfLibrary = WebKitHtmlToPdfLibrary.INSTANCE;

    private Engine() {
        executorService = Executors.newFixedThreadPool(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
    }

    public Future<?> doWork(Runnable task) {
        return executorService.submit(() -> {
            start();
            task.run();
        });
    }

    public static Engine getInstance() {
        return INSTANCE;
    }

    public void start() {
        if(status == Status.STOPPED) {
            throw new IllegalStateException("engine already stopped");
        } else if(status == Status.INACTIVE) {
            int init = imageLibrary.wkhtmltoimage_init(0);
            LOGGER.debug("image library started {}", init == 1 ? "OK" : "FAIL");
            init = pdfLibrary.wkhtmltopdf_init(0);
            LOGGER.debug("pdf library started {}", init == 1 ? "OK" : "FAIL");
            status = Status.ACTIVE;
        }
    }

    public void stop() {
        if(status == Status.ACTIVE) {
            imageLibrary.wkhtmltoimage_deinit();
            pdfLibrary.wkhtmltopdf_deinit();
            status = Status.STOPPED;
            LOGGER.debug("engine stopped");
        }
    }

    enum Status {
        ACTIVE,
        INACTIVE,
        STOPPED
    }
}
