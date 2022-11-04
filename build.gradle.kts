// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        maven { setUrl("https://maven.aliyun.com/repository/central/") }
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.2.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    }
}

allprojects {
    repositories {
        maven { setUrl("https://maven.aliyun.com/repository/central/") }
        google()
    }
    group = "com.github.leifzhang"
    configurations.all {
        resolutionStrategy.dependencySubstitution.all {
            if (requested is ModuleComponentSelector) {
                val moduleRequested = requested as ModuleComponentSelector
                val p = rootProject.allprojects.find { p ->
                    (p.group == moduleRequested.group && p.name == moduleRequested.module)
                }
                if (p != null) {
                    useTarget(project(p.path), "selected local project")
                }

            }
        }
    }
}
