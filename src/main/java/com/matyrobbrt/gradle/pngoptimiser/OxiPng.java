package com.matyrobbrt.gradle.pngoptimiser;

import com.matyrobbrt.gradle.pngoptimiser.util.Os;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.gradle.api.invocation.Gradle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OxiPng {
    private final Map<String, Object> options = new HashMap<>();
    private final Project project;

    public OxiPng(Project project) {
        this.project = project;
    }

    public OxiPng option(String name, Object value) {
        if (name.length() == 1)
            options.put("-" + name, value);
        else
            options.put("--" + name, value);
        return this;
    }

    public static Path getOrCreateOxiPng(Gradle gradle) throws IOException {
        final var os = Os.getOs();
        if (os == Os.MAC) // TODO Add Mac support
            throw new UnsupportedOperationException("MacOS is currently unsupported by PNGOptimizer!");
        final var dir = Objects.requireNonNull(gradle.getGradleUserHomeDir()).toPath().resolve("oxipng");
        final var path = dir.resolve("oxipng-" + PNGOptimiser.VERSION + (os == Os.WINDOWS ? ".exe" : "")).toAbsolutePath();
        if (Files.exists(path))
            return path;
        InputStream oxipng;
        if (os == Os.WINDOWS) {
            oxipng = PNGOptimiser.class.getResourceAsStream("/oxipng_windows.exe");
        } else
            oxipng = PNGOptimiser.class.getResourceAsStream("/oxipng_linux");
        Files.createDirectories(path.getParent());
        Files.write(path, IOUtils.toByteArray(Objects.requireNonNull(oxipng)));
        return path;
    }

    public List<String> commandLine() throws IOException {
        final var execPath = getOrCreateOxiPng(project.getGradle());
        final var command = new ArrayList<String>();
        command.add(execPath.toString());
        options.forEach((key, value) -> {
            command.add(key);
            command.add(value.toString());
        });
        return command;
    }

    public Process run(List<String> files) throws Exception {
        final var command = commandLine();
        command.addAll(files);

        return new ProcessBuilder(command)
            .inheritIO()
            .start();
    }
}
