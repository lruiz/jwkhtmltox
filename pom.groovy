
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

project {
    modelVersion '4.0.0'
    parent 'org.tautua:tautua:1'

    groupId 'org.tautua.jwkhtmltox'
    artifactId 'jwkhtmltox'
    version '1.0-SNAPSHOT'
    properties {
        'java.version' '1.8'
    }
    dependencies {
        dependency 'net.java.dev.jna:jna:5.8.0'
        dependency 'org.slf4j:slf4j-api:1.7.30'

        dependency ('ch.qos.logback:logback-classic:1.2.3') {
            scope 'test'
        }
        dependency ('org.junit.jupiter:junit-jupiter-engine:5.6.2') {
            scope 'test'
        }
    }
    build {
        plugins {
//            plugin('com.nativelibs4java:maven-jnaerator-plugin:0.12') {
//                executions {
//                    execution {
//                        goals 'generate'
//                    }
//                }
//            }
        }
    }
}
