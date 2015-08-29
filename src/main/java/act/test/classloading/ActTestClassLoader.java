package act.test.classloading;

import act.app.App;
import act.asm.ClassReader;
import act.asm.ClassWriter;
import act.test.util.ActTestRunner;
import act.util.AsmByteCodeEnhancer;
import act.util.ByteCodeVisitor;
import org.osgl._;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.IO;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class ActTestClassLoader extends URLClassLoader {

    private C.List<ByteCodeEnhancer> enhancerList = C.newList();
    private App app;
    private String[] classNamePattern;

    public ActTestClassLoader(App app, String[] actClassNamePatterns) {
        super(urls(ActTestRunner.class.getClassLoader()), ActTestRunner.class.getClassLoader());
        this.app = _.notNull(app);
        this.classNamePattern = _.notNull(actClassNamePatterns);
    }

    public ActTestClassLoader app(App app) {
        this.app = _.notNull(app);
        addEnhancer(ByteCodeEnhancer.CONTROLLER);
        addEnhancer(ByteCodeEnhancer.MAILER);
        return this;
    }

    public void addEnhancer(ByteCodeEnhancer enhancer) {
        E.NPE(enhancer);
        if (!enhancerList.contains(enhancer)) {
            enhancerList.add(enhancer);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (null != c) {
            return c;
        }
        if (isProtectedClass(name)) {
            return getParent().loadClass(name);
        }
        return findClass(name);
    }

    private boolean isProtectedClass(String name) {
        if (classNamePattern.length > 0) {
            for (String s : classNamePattern) {
                if (name.startsWith(s)) {
                    return true;
                }
                if (name.matches(s)) {
                    return true;
                }
            }
            return true;
        }
        return name.startsWith("java") || name.startsWith("sun") || name.startsWith("org.junit");
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        String path = name.replace('.', '/').concat(".class");
        if (name.contains("MockitoConfiguration")) {
            _.nil();
        }
        URL res = getResource(path);
        if (res != null) {
            try {
                return defineClass(name, res);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        } else {
            throw new ClassNotFoundException(name);
        }
    }

    private Class defineClass(String name, URL res) throws IOException {
        byte[] ba = IO.readContent(res.openStream());
        return defineClassX(name, ba);
    }

    private ByteCodeVisitor enhancer(String className, _.Var<ClassWriter> cw) {
        List<AsmByteCodeEnhancer> l = C.newList();
        for (ByteCodeEnhancer e : enhancerList) {
            l.add(e.enhancer(app));
        }
        return ByteCodeVisitor.chain(cw, l);
    }

    private Class defineClassX(String name, byte[] bytecode) {
        Class<?> c;
        _.Var<ClassWriter> cw = _.val(null);
        ByteCodeVisitor enhancer = enhancer(name, cw);
        if (null == enhancer) {
            c = defineClassX(name, bytecode, 0, bytecode.length);
        } else {
            ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cw.set(w);
            enhancer.commitDownstream();
            ClassReader r;
            r = new ClassReader(bytecode);
            try {
                r.accept(enhancer, 0);
                byte[] baNew = w.toByteArray();
                c = defineClassX(name, baNew, 0, baNew.length);
            } catch (RuntimeException e) {
                throw e;
            } catch (Error e) {
                throw e;
            } catch (Exception e) {
                throw E.unexpected("Error processing class " + name);
            }
        }
        return c;
    }

    private static URL[] urls(ClassLoader parent) {
        URLClassLoader ucl = _.cast(parent);
        return ucl.getURLs();
    }

    protected Class<?> defineClassX(String name, byte[] b, int off, int len) {
        int i = name.lastIndexOf('.');
        if (i != -1) {
            String pkgName = name.substring(0, i);
            // Check if package already loaded.
            if (getPackage(pkgName) == null) {
                try {
                    definePackage(pkgName, null, null, null, null, null, null, null);
                } catch (IllegalArgumentException iae) {
                    throw new AssertionError("Cannot find package " +
                            pkgName);
                }
            }
        }
        return super.defineClass(name, b, off, len);
    }
}
