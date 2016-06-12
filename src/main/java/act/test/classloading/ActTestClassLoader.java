package act.test.classloading;

import act.app.App;
import act.app.AppByteCodeScanner;
import act.app.AppClassLoader;
import act.app.AppCodeScannerManager;
import act.asm.ClassReader;
import act.asm.ClassWriter;
import act.conf.AppConfig;
import act.controller.bytecode.ControllerByteCodeScanner;
import act.controller.meta.ControllerClassMetaInfo;
import act.controller.meta.ControllerClassMetaInfoManager;
import act.event.EventBus;
import act.job.AppJobManager;
import act.job.bytecode.JobByteCodeScanner;
import act.job.meta.JobClassMetaInfoManager;
import act.mail.bytecode.MailerByteCodeScanner;
import act.mail.meta.MailerClassMetaInfoManager;
import act.route.Router;
import act.test.util.ActTestRunner;
import act.util.AsmByteCodeEnhancer;
import act.util.ByteCodeVisitor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgl.$;
import org.osgl.exception.UnexpectedException;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.IO;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActTestClassLoader extends URLClassLoader {

    private C.List<ByteCodeEnhancer> enhancerList = C.newList();
    private App app;
    private List<String> classNamePattern;
    protected ControllerClassMetaInfoManager controllerInfo;
    protected MailerClassMetaInfoManager mailerInfo = new MailerClassMetaInfoManager();
    protected JobClassMetaInfoManager jobInfo = new JobClassMetaInfoManager();
    private AppCodeScannerManager scannerManager;
    private Properties testConf;

    public ActTestClassLoader(App app, List<String> actClassNamePatterns) {
        super(urls(ActTestRunner.class.getClassLoader()), ActTestRunner.class.getClassLoader());
        this.classNamePattern = $.notNull(actClassNamePatterns);
        this.controllerInfo = new ControllerClassMetaInfoManager(app);
        this.app(app);
    }

    public ActTestClassLoader testProperties(Properties p) {
        testConf = p;
        return this;
    }

    public ActTestClassLoader app(App app) {
        this.app = $.notNull(app);
        AppClassLoader cl = mock(AppClassLoader.class);
        when(app.classLoader()).thenReturn(cl);
        when(cl.controllerClassMetaInfo(Mockito.anyString())).thenAnswer(new Answer<ControllerClassMetaInfo>() {
            @Override
            public ControllerClassMetaInfo answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return controllerInfo.controllerMetaInfo((String) args[0]);
            }
        });
        when(cl.controllerClassMetaInfoManager()).thenReturn(controllerInfo);
        when(cl.mailerClassMetaInfoManager()).thenReturn(mailerInfo);
        when(cl.jobClassMetaInfoManager()).thenReturn(jobInfo);

        EventBus bus = mock(EventBus.class);
        when(app.eventBus()).thenReturn(bus);

        AppConfig config = mock(AppConfig.class);
        when(config.get(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                String key = (String) invocationOnMock.getArguments()[0];
                return testConf.getProperty(key);
            }
        });
        when(app.config()).thenReturn(config);
        when(config.possibleControllerClass(anyString())).thenReturn(true);
        when(config.jobPoolSize()).thenReturn(2);

        Router router = mock(Router.class);
        when(app.router()).thenReturn(router);
        when(app.router(anyString())).thenReturn(router);
        when(router.possibleController(anyString())).thenReturn(true);

        AppJobManager jobManager = new AppJobManager(app);
        when(app.jobManager()).thenReturn(jobManager);

        scannerManager = new AppCodeScannerManager(app);
        scannerManager.register(new ControllerByteCodeScanner());
        scannerManager.register(new MailerByteCodeScanner());
        scannerManager.register(new JobByteCodeScanner());
        addEnhancer(ByteCodeEnhancer.CONTROLLER);
        addEnhancer(ByteCodeEnhancer.MAILER);
        //addEnhancer(ByteCodeEnhancer.DB_ENTITY_CLASS);
        //addEnhancer(ByteCodeEnhancer.DATA_OBJECT);
        //addEnhancer(ByteCodeEnhancer.SINGLETON);
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
        if (!classNamePattern.isEmpty()) {
            for (String s : classNamePattern) {
                if (name.startsWith(s)) {
                    return false;
                }
                if (name.matches(s)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        String path = name.replace('.', '/').concat(".class");
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

    private ByteCodeVisitor enhancer(String className, $.Var<ClassWriter> cw) {
        List<AsmByteCodeEnhancer> l = C.newList();
        for (ByteCodeEnhancer e : enhancerList) {
            l.add(e.enhancer(app));
        }
        return ByteCodeVisitor.chain(cw, l);
    }

    private Class defineClassX(String name, byte[] bytecode) {
        Class<?> c;
        $.Var<ClassWriter> cw = $.var(null);
        ByteCodeVisitor enhancer = enhancer(name, cw);
        if (null == enhancer) {
            c = defineClassX(name, bytecode, 0, bytecode.length);
        } else {
            // try to scan code
            List<ByteCodeVisitor> visitors = C.newList();
            List<AppByteCodeScanner> scanners = C.newList();
            for (AppByteCodeScanner scanner : scannerManager.byteCodeScanners()) {
                if (scanner.start(name)) {
                    visitors.add(scanner.byteCodeVisitor());
                    scanners.add(scanner);
                }
            }
            ByteCodeVisitor theVisitor = ByteCodeVisitor.chain(visitors);
            ClassReader cr = new ClassReader(bytecode);
            try {
                cr.accept(theVisitor, 0);
            } catch (UnexpectedException e) {
                Throwable t = e.getCause();
                if (t instanceof ClassNotFoundException) {
                    //continue;
                } else {
                    throw e;
                }
            }
            for (AppByteCodeScanner scanner : scanners) {
                scanner.scanFinished(name);
            }
            // start to enhance
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
        URLClassLoader ucl = $.cast(parent);
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
