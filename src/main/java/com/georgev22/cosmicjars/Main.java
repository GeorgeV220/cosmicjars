package com.georgev22.cosmicjars;

import com.georgev22.cosmicjars.annotations.MavenLibrary;
import com.georgev22.cosmicjars.annotations.Repository;
import com.georgev22.cosmicjars.utilities.LibraryLoader;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Repository(repoName = "central", repoURL = "https://repo.maven.apache.org/maven2/")
@Repository(repoName = "georgev22", repoURL = "https://repo.georgev22.com/releases")

@MavenLibrary("org.apache.commons:commons-compress:1.26.1")
@MavenLibrary("com.google.guava:guava:33.1.0-jre")
@MavenLibrary("commons-lang:commons-lang:2.6")
@MavenLibrary("com.google.code.gson:gson:2.10.1")
@MavenLibrary("com.github.oshi:oshi-core:6.6.0")
@MavenLibrary("org.jfree:jfreechart:1.5.4")
@MavenLibrary("com.georgev22.library:yaml:11.14.0")
@MavenLibrary("com.formdev:flatlaf:3.4.1")
@MavenLibrary("com.formdev:flatlaf-intellij-themes:3.4.1")
@MavenLibrary("org.jetbrains:annotations:24.0.0")
@MavenLibrary("org.apache.logging.log4j:log4j-core:2.23.1")
@MavenLibrary("org.apache.logging.log4j:log4j-api:2.23.1")
@MavenLibrary("org.jline:jline:3.25.1")
public class Main {

    public static void main(String[] args) {
        try (InputStream stream = Main.class.getResourceAsStream("/logging.properties")) {
            if (stream == null) {
                throw new RuntimeException("Cannot find logging.properties file");
            }
            LogManager.getLogManager().readConfiguration(stream);
        } catch (Exception e) {
            throw new RuntimeException("Error loading logging.properties file", e);
        }

        // Load dependencies
        Repository[] repositories = Main.class.getAnnotationsByType(Repository.class);
        List<LibraryLoader.CosmicDependency> dependencies = getCosmicDependencies();

        LibraryLoader libraryLoader = new LibraryLoader(
                Logger.getGlobal(),
                Arrays.stream(repositories).toList()
        );

        libraryLoader.load(dependencies);

        CosmicJars.main(args);

    }

    private static @NotNull List<LibraryLoader.CosmicDependency> getCosmicDependencies() {
        MavenLibrary[] libs = Main.class.getDeclaredAnnotationsByType(MavenLibrary.class);
        List<LibraryLoader.CosmicDependency> dependencies = new ArrayList<>();

        for (MavenLibrary lib : libs) {
            if (
                    !lib.groupId().equalsIgnoreCase("") ||
                            !lib.artifactId().equalsIgnoreCase("") ||
                            !lib.version().equalsIgnoreCase("")
            ) {
                dependencies.add(new LibraryLoader.CosmicDependency(lib.groupId(), lib.artifactId(), lib.version()));
            } else {
                String[] dependency = lib.value().split(":", 3);
                dependencies.add(new LibraryLoader.CosmicDependency(dependency[0], dependency[1], dependency[2]));
            }
        }
        return dependencies;
    }

}
