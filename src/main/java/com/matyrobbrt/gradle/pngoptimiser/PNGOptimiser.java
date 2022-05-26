package com.matyrobbrt.gradle.pngoptimiser;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PNGOptimiser implements Plugin<Project> {
    public static final String VERSION = PNGOptimiser.class.getPackage().getImplementationVersion();
    @Override
    public void apply(Project project) {
    }
}
