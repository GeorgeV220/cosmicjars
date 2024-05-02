package com.georgev22.cosmicjars.utilities;

import com.georgev22.cosmicjars.annotations.Repository;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LibraryLoader {

    private final Logger logger;
    private final RepositorySystem repository;
    private final DefaultRepositorySystemSession session;
    private final List<RemoteRepository> repositories;

    public LibraryLoader(@NotNull Logger logger, @NotNull List<Repository> repositories) {
        this.logger = logger;

        //noinspection deprecation
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        this.repository = locator.getService(RepositorySystem.class);
        this.session = MavenRepositorySystemUtils.newSession();

        session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_FAIL);
        session.setLocalRepositoryManager(repository.newLocalRepositoryManager(session, new LocalRepository("./cosmicJars/libraries")));
        session.setTransferListener(new AbstractTransferListener() {
            @Override
            public void transferStarted(@NotNull TransferEvent event) {
                logger.log(Level.INFO, "Downloading {0}", event.getResource().getRepositoryUrl() + event.getResource().getResourceName());
            }
        });

        session.setSystemProperties(System.getProperties());
        session.setReadOnly();

        this.repositories = repository.newResolutionRepositories(
                session,
                repositories.stream().map(repo -> new RemoteRepository.Builder(repo.repoName(), repo.type(), repo.repoURL()).build()).collect(Collectors.toList())
        );
    }

    public void load(@NotNull List<CosmicDependency> lCosmicDependencies) {
        if (lCosmicDependencies.isEmpty()) {
            return;
        }
        logger.log(Level.INFO, "Loading {0} libraries... please wait", new Object[]
                {
                        lCosmicDependencies.size()
                });

        List<Dependency> dependencies = new ArrayList<>();
        for (String library : lCosmicDependencies.stream()
                .map(dependency -> dependency.groupId + ":" + dependency.artifactId + ":" + dependency.version)
                .toList()) {
            Artifact artifact = new DefaultArtifact(library);
            Dependency dependency = new Dependency(artifact, null);

            dependencies.add(dependency);
        }

        DependencyResult result;
        try {
            result = repository.resolveDependencies(session, new DependencyRequest(new CollectRequest((Dependency) null, dependencies, repositories), null));
        } catch (DependencyResolutionException ex) {
            throw new RuntimeException("Error resolving libraries", ex);
        }

        List<URL> jarFiles = new ArrayList<>();
        for (ArtifactResult artifact : result.getArtifactResults()) {
            File file = artifact.getArtifact().getFile();

            URL url;
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException ex) {
                throw new AssertionError(ex);
            }

            jarFiles.add(url);
            logger.log(Level.INFO, "Downloaded library {0}", new Object[]
                    {
                            file
                    });
        }

        ClassLoaderAccess classLoaderAccess = new ClassLoaderAccess(this.getClass().getClassLoader());

        for (URL url : jarFiles) {
            classLoaderAccess.add(url);
            logger.log(Level.INFO, "Loaded library {0}", url);
        }
    }

    @NotNull
    public record CosmicDependency(String groupId, String artifactId, String version) {

        /**
         * Constructs a new Dependency with the given group ID, artifact ID, version, and repository URL.
         *
         * @param groupId    the group ID of the dependency
         * @param artifactId the artifact ID of the dependency
         * @param version    the version of the dependency
         */
        public CosmicDependency {
        }
    }
}
