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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.*;

public class ThreadSafeTest {
    private static Logger LOGGER = LoggerFactory.getLogger(ThreadSafeTest.class);

    @Test
    public void buildImageFromHtmlToJpg() throws IOException {
        ExecutorService executor = new ThreadPoolExecutor(5, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2));
        int threads = 6;
        CountDownLatch latch = new CountDownLatch(6);
        for(int i = 0; i < threads; i++) {
            String fileName = "target/helloworld-" + i + ".jpeg";
            executor.execute(() -> {
                LOGGER.debug("task {} started", fileName);
                InputStream input = null;
                try {
                    input = new FileInputStream("src/test/resources/helloworld.html");
                    OutputStream output = new FileOutputStream(fileName);
                    new ImageBuilder()
                            .fromInput(input)
                            .toOutput(output)
                            .withFormat(ImageFormat.JPG)
                            .withDimensions(650, 570)
                            .withQuality(1)
                            .build();
                    output.close();
                    latch.countDown();
                    LOGGER.debug("task {} ended", fileName);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });
        }

        try {
            latch.await(1, TimeUnit.MINUTES);
        } catch(InterruptedException e) {
            System.out.println("count left " + latch.getCount());
            e.printStackTrace();
        }
    }
}
