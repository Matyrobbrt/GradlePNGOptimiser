# GradlePNGOptimiser
A Gradle plugin based on [OxiPng](https://github.com/shssoichiro/oxipng) which adds tasks in order to optimise png files.

## Installing
To start, in your root build.gradle, add the following lines in order to install the plugin:
```groovy
plugins {
    id 'com.matyrobbrt.pngoptimiser' version "$pngOptimiserVersion" // The plugin is located at the Gradle plugin portal
    // The latest version can be found at https://plugins.gradle.org/plugin/com.matyrobbrt.pngoptimiser
}
```
## Configuring tasks
This plugins provides one `OptimisePNGTask` task which can configure OxiPng, and decide what files should be optimised:
```groovy
tasks.register('optimisePng', com.matyrobbrt.gradle.pngoptimiser.task.OptimisePNGTask) {
  file(project.fileTree(dir: file('images'), includes: ['**/*.png'])) // Optimise all png files in the `images` directory
  option 'o', 6 // Use optimisation of type 6
}
```

All the available options can be found on the [OxiPng Wiki](https://github.com/shssoichiro/oxipng#usage)
