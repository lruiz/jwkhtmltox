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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Engine {
    private static final Engine INSTANCE = new Engine();
    private final ExecutorService executorService;


    private Engine() {
        executorService = Executors.newFixedThreadPool(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
    }

    public Future<?> doWork(Runnable task) {
        return executorService.submit(task);
    }

    public static Engine getInstance() {
        return INSTANCE;
    }

    public void start() {

    }

    public void stop() {

    }
}
