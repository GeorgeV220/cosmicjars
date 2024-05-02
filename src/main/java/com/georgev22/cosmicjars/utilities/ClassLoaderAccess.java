package com.georgev22.cosmicjars.utilities;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides access to {@link ClassLoader} to add URLs on runtime.
 **/
public class ClassLoaderAccess {
    private final Collection<URL> unopenedURLs;
    private final Collection<URL> pathURLs;

    private final ClassLoader classLoader;

    private Logger logger = null;

    private static volatile Object theUnsafe;

    static {
        try {
            synchronized (ClassLoaderAccess.class) {
                if (theUnsafe == null) {
                    Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                    Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
                    theUnsafeField.setAccessible(true);
                    theUnsafe = theUnsafeField.get(null);
                }
            }
        } catch (Exception e) {
            theUnsafe = null;
        }
    }

    private static boolean isUnsafeAvailable() {
        return theUnsafe != null;
    }


    /**
     * Creates a {@link ClassLoaderAccess} for the given URLClassLoader.
     *
     * @param classLoader the class loader
     */
    @SuppressWarnings({"unchecked", "CallToPrintStackTrace"})
    public ClassLoaderAccess(URLClassLoader classLoader) {
        this.classLoader = classLoader;
        Collection<URL> unopenedURLs;
        Collection<URL> pathURLs;
        try {
            Object ucp = fetchField(classLoader.getClass(), classLoader, "ucp");
            unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
            pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
        } catch (Throwable e) {
            try {
                Object ucp = fetchField(classLoader.getClass(), classLoader, "ucp");
                unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "urls");
                pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
            } catch (Throwable e1) {
                unopenedURLs = null;
                pathURLs = null;
                if (this.logger != null) {
                    this.logger.log(Level.SEVERE, "Failed to get unopenedUrls and pathUrls from " + this.classLoader.getClass().getPackage().getName() + "." + this.classLoader.getClass().getName(), e);
                    this.logger.log(Level.SEVERE, "Failed to get unopenedUrls and pathUrls from " + this.classLoader.getClass().getPackage().getName() + "." + this.classLoader.getClass().getName(), e1);
                } else {
                    e.printStackTrace();
                    e1.printStackTrace();
                }
            }
        }
        this.unopenedURLs = unopenedURLs;
        this.pathURLs = pathURLs;
    }

    /**
     * Creates a {@link ClassLoaderAccess} for the given ClassLoader.
     *
     * @param classLoader the class loader
     */
    @SuppressWarnings({"unchecked", "CallToPrintStackTrace"})
    public ClassLoaderAccess(ClassLoader classLoader) {
        this.classLoader = classLoader;
        Collection<URL> unopenedURLs;
        Collection<URL> pathURLs;
        try {
            Object ucp = fetchField(classLoader.getClass().getSuperclass(), classLoader, "ucp");
            unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
            pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
        } catch (Throwable e) {
            try {
                Object ucp = fetchField(classLoader.getClass().getSuperclass(), classLoader, "ucp");
                unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "urls");
                pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
            } catch (Throwable e1) {
                unopenedURLs = null;
                pathURLs = null;
                if (this.logger != null) {
                    this.logger.log(Level.SEVERE, "Failed to get unopenedUrls and pathUrls from " + this.classLoader.getClass().getPackage().getName() + "." + this.classLoader.getClass().getName(), e);
                    this.logger.log(Level.SEVERE, "Failed to get unopenedUrls and pathUrls from " + this.classLoader.getClass().getPackage().getName() + "." + this.classLoader.getClass().getName(), e1);
                } else {
                    e.printStackTrace();
                    e1.printStackTrace();
                }
            }
        }
        this.unopenedURLs = unopenedURLs;
        this.pathURLs = pathURLs;
    }

    /**
     * Adds the given URL to the class loader.
     *
     * @param url the URL to add
     */
    public void add(@NotNull URL url) throws RuntimeException {
        if (this.unopenedURLs.contains(url) || this.pathURLs.contains(url)) {
            throw new RuntimeException("URL " + url + " already exists in the path");
        }
        this.unopenedURLs.add(url);
        this.pathURLs.add(url);
    }

    /**
     * Removes the given URL from the class loader.
     *
     * @param url the URL to remove
     */
    public void remove(@NotNull URL url) {
        this.unopenedURLs.remove(url);
        this.pathURLs.remove(url);
    }

    public Collection<URL> getPathURLs() {
        return pathURLs;
    }

    public Collection<URL> getUnopenedURLs() {
        return unopenedURLs;
    }

    /**
     * Check if a URL is present to the unopenedURLs or pathURLs.
     *
     * @param url URL to check
     * @return if the URL is present to the unopenedURLs or pathURLs.
     */
    public boolean contains(URL url) {
        if (unopenedURLs.contains(url)) {
            return true;
        } else return pathURLs.contains(url);
    }

    /**
     * Set the logger for this class.
     *
     * @param logger the logger to be set
     */
    public void registerLogger(Logger logger) {
        this.logger = logger;
    }

    private Object fetchField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (isUnsafeAvailable()) {
            Field field = clazz.getDeclaredField(name);
            long offset = (long) fetchMethodAndInvoke(theUnsafe.getClass(), "objectFieldOffset", theUnsafe, new Object[]{field}, new Class[]{Field.class});
            return fetchMethodAndInvoke(theUnsafe.getClass(), "getObject", theUnsafe, new Object[]{object, offset}, new Class[]{Object.class, long.class});
        }
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnsupportedOperationException();
        }
    }

    private Object fetchMethodAndInvoke(final Class<?> clazz, final String name, Object obj, Object[] arguments, Class<?>[] parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return fetchMethod(clazz, name, parameterTypes).invoke(obj, arguments);
    }

    private @NotNull Method fetchMethod(final @NotNull Class<?> clazz, final String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        if (!isUnsafeAvailable()) {
            method.setAccessible(true);
        }
        return method;
    }

    @Override
    public String toString() {
        return "ClassLoaderAccess{" +
                "unopenedURLs=" + unopenedURLs +
                ", pathURLs=" + pathURLs +
                ", classLoader=" + classLoader.getClass().getPackage().getName() + "." + classLoader.getClass().getName() +
                '}';
    }
}
