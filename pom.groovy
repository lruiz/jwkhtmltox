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
