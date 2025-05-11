# Getting Started

Welcome to **AbyssalLib**!

(This API and its docs are NOT complete, they may change, and more will be added over time)

To start using this framework in your project, you'll need to add the dependency via JitPack.

## 🔧 Installation

1. Add the JitPack repository to your project's build system.
    <tabs>
    <tab title="Gradle">
   For Gradle, add this to your `build.gradle`:

    ```gradle
    repositories {
        maven { url 'https://jitpack.io' }
    }
    ```
   </tab>

    <tab title="Maven">
   For Maven, add this to your `pom.xml`:

    ```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    ```
   </tab>
    </tabs>

2. Add the `AbyssalLib` dependency to your project.

   <tabs>
   <tab title="Gradle">

   For Gradle, add this to your `build.gradle`:

   ```gradle
    dependencies {
        implementation 'com.github.darksoulq:AbyssalLib:Version'
    }
    ```
   </tab>
   <tab title="Maven">
   For Maven, add this to your `pom.xml`:

    ```xml
    <dependencies>
        <dependency>
            <groupId>com.github.darksoulq</groupId>
            <artifactId>AbyssalLib</artifactId>
            <version>Version</version>
        </dependency>
    </dependencies>
    ```
   </tab>
   </tabs>

   *(Replace `Version` with the specific version tag you wish to use.)*

You're now ready to start building plugins with AbyssalLib!
