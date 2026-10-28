import java.net.URI

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "DS Helper GitHubPackages"
            url = uri("https://maven.pkg.github.com/dsoft2010/helper_aos")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}

rootProject.name = "helper-aos-samples"

include(":sample-web")
include(":sample-hybrid-activity")
include(":sample-hybrid-fragment")
include(":sample-network")
