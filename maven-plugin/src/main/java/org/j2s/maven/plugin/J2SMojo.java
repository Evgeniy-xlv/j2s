package org.j2s.maven.plugin;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.j2s.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "j2s-mojo", defaultPhase = LifecyclePhase.COMPILE)
public class J2SMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    public MavenProject project;

    private ClassLoader classLoader;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        classLoader = getClassLoader();
        try {
            List classpathElements = project.getCompileClasspathElements();
            for (Object classpathElement : classpathElements) {
                File file = new File((String) classpathElement);
                List<Class<?>> classes = new ArrayList<>();
                scan((String) classpathElement, classes, file);
                for (Class<?> aClass : classes) {
                    J2SLibrary annotation = aClass.getAnnotation(J2SLibrary.class);
                    List<J2SCompilationResult> compile = J2SCompiler.compile(aClass, J2SCompilationType.TYPESCRIPT);
                    J2SLibPublisher.publish(annotation.name(), annotation.version(), annotation.description(), J2SCompilationType.TYPESCRIPT, compile);
                }
            }
        } catch (DependencyResolutionRequiredException | IOException e) {
            throw new MojoExecutionException("Error ", e);
        }
    }

    private void scan(String startPath, List<Class<?>> classes, File... files) {
        for (File file : files) {
            if (file.isDirectory()) {
                File[] files1 = file.listFiles();
                if (files1 != null)
                    scan(startPath, classes, files1);
            } else if(file.getAbsolutePath().endsWith(".class")) {
                String path = file.getAbsolutePath()
                        .replace(startPath + "\\", "")
                        .replace("\\", ".");
                path = path.substring(0, path.lastIndexOf("."));
                try {
                    Class<?> aClass = classLoader.loadClass(path);
                    if (aClass.isAnnotationPresent(J2SLibrary.class))
                        classes.add(aClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ClassLoader getClassLoader() {
        try {
            List classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            URL urls[] = new URL[classpathElements.size()];
            for (int i = 0; i < classpathElements.size(); ++i)
                urls[i] = new File((String) classpathElements.get(i)).toURL();
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (Exception e) {
            getLog().debug("Couldn't get the classloader.");
            return this.getClass().getClassLoader();
        }
    }
}
