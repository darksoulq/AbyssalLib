# Getting Started

Welcome to the **AbyssalLib**!

(This api and its docs are NOT complete, they may change, and more will be added over time)

To start using this framework in your project, you'll need to add the provided `.jar` file as a library. This allows you to access the modding API's full feature set directly from your own plugin.

## 🔧 Installation

1. Download the latest version of the `AbyssalLib.jar` from the releases page or your distribution source.
2. Drop the JAR into your project's `libs/` folder (or wherever you store external libraries).
3. Make sure your build system (e.g., Maven, Gradle, or your IDE's project settings) includes the JAR in your classpath.

## Example (IntelliJ IDEA):

- Go to `File > Project Structure > Modules`.
- Click the `Dependencies` tab.
- Click the `+` icon → `JARs or directories...`.
- Select your `AbyssalLib.jar`.
- Click OK and make sure it's marked as "Compile" scope.

You're now ready to start building plugins with AbyssalLib!

(the api will be available on maven central once its stable)