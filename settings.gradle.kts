pluginManagement {
    //includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PhotoSearchApp"

include(":core:base")
include(":core:network")
include(":core:common")

include(":library:navigation")
include(":library:ui")

include(":app")
include(":domain")
include(":data")
include(":hilt")

include(":feature:photo-list")
include(":feature:photo-search")
