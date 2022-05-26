package com.matyrobbrt.gradle.pngoptimiser.task;

import com.matyrobbrt.gradle.pngoptimiser.OxiPng;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class OptimisePNGTask extends DefaultTask implements Runnable {
    public static final int TERMINAL_LIMIT = 8000;

    @Internal
    private final OxiPng oxiPng = new OxiPng(getProject());
    private final List<File> files = new ArrayList<>();

    public void file(File file) {
        this.files.add(file);
    }
    public void files(File... files) {
        this.files.addAll(List.of(files));
    }
    public void file(FileTree fileTree) {
        this.files.addAll(fileTree.getFiles());
    }
    public void files(FileTree... fileTrees) {
        this.files.addAll(Arrays.stream(fileTrees).flatMap(f -> f.getFiles().stream()).toList());
    }
    public void file(Object file) {
        this.files.add(getProject().file(file));
    }
    public void files(Object... files) {
        this.files.addAll(getProject().files(files).getFiles());
    }

    public OxiPng getOxiPng() {
        return oxiPng;
    }
    public void option(String name, Object value) {
        oxiPng.option(name, value);
    }

    public void configureOxiPng(Action<OxiPng> action) {
        action.execute(oxiPng);
    }

    public void configureOxiPng(Closure<?> closure) {
        closure.call(oxiPng);
    }

    @Override
    @TaskAction
    public void run() {
        try {
            getProject().getLogger().warn("Optimising {} files... This may take a while.", files.size());
            var oldSize = BigInteger.valueOf(0L);
            final var filesCopy = new ArrayList<>(files);
            var currentCmdLength = oxiPng.commandLine().stream().mapToInt(String::length).sum();
            var currentFiles = new ArrayList<File>();
            while (!filesCopy.isEmpty()) {
                final var file = filesCopy.get(0);
                oldSize = oldSize.add(BigInteger.valueOf(file.length()));
                if (currentCmdLength + file.toString().length() >= TERMINAL_LIMIT) {
                    runOxiPng(currentFiles);
                    currentCmdLength = oxiPng.commandLine().stream().mapToInt(String::length).sum();
                    currentFiles = new ArrayList<>();
                }
                currentCmdLength += file.toString().length();
                currentFiles.add(file);
                filesCopy.remove(0);
            }
            if (!currentFiles.isEmpty()) {
                runOxiPng(currentFiles);
            }

            var newSize = BigInteger.valueOf(0L);
            for (final var file : files)
                newSize = newSize.add(BigInteger.valueOf(file.length()));
            System.out.println(oldSize.longValue());
            System.out.println(newSize.longValue());
            if (newSize.compareTo(oldSize) < 0)
                getProject().getLogger().warn("Reduced total file sizes by {}", String.format("%.2f%%", (oldSize.doubleValue() - newSize.doubleValue()) * 100 / oldSize.doubleValue()));
        } catch (Exception e) {
            getProject().getLogger().error("Exception trying to optimize files: ", e);
            throw new RuntimeException(e);
        }
    }

    private void runOxiPng(List<File> currentFiles) throws Exception {
        final var process = oxiPng.run(currentFiles.stream().map(File::toString).toList());
        getProject().getLogger().debug("Started Oxipng process with id '{}'", process.pid());
        final var pHandle = ProcessHandle.of(process.pid());
        while (pHandle.map(ProcessHandle::isAlive).orElse(false))
            Thread.onSpinWait();
    }
}
