package act.test.util;

import act.app.ActionContext;
import act.app.App;
import act.conf.AppConfig;
import act.event.EventBus;
import act.job.AppJobManager;
import act.route.Router;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgl._;
import org.osgl.http.H;
import org.osgl.util.S;

import java.lang.reflect.Field;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@Ignore
@RunWith(ActTestRunner.class)
public class ActTestBase extends Assert {

    protected App mockApp;
    protected Router mockRouter;
    protected ActionContext mockActionContext;
    protected AppConfig mockAppConfig;
    protected AppJobManager mockJobManager;
    protected H.Request mockReq;
    protected H.Response mockResp;
    protected EventBus mockEventBus;

    @Before
    public void _before() throws Exception {
        mockApp = mock(App.class);
        Field f = App.class.getDeclaredField("INST");
        f.setAccessible(true);
        f.set(null, mockApp);
        mockJobManager = mock(AppJobManager.class);
        mockEventBus = mock(EventBus.class);
        when(mockApp.jobManager()).thenReturn(mockJobManager);
        when(mockApp.eventBus()).thenReturn(mockEventBus);
        mockAppConfig = mock(AppConfig.class);
        mockActionContext = mock(ActionContext.class);
        when(mockActionContext.app()).thenReturn(mockApp);
        when(mockActionContext.config()).thenReturn(mockAppConfig);
        when(mockActionContext.newInstance(any(Class.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return _.newInstance((Class) args[0]);
            }
        });
        mockRouter = mock(Router.class);
        when(mockApp.config()).thenReturn(mockAppConfig);
        when(mockApp.router()).thenReturn(mockRouter);
        when(mockApp.newInstance(any(Class.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return _.newInstance((Class) args[0]);
            }
        });
        mockReq = mock(H.Request.class);
        mockResp = mock(H.Response.class);
        setup();
    }

    protected void setup() {}


    protected void isNull(Object a) {
        assertNull(a);
    }

    protected void notNull(Object a) {
        assertNotNull(a);
    }

    protected void same(Object a, Object b) {
        assertSame(a, b);
    }

    protected void eq(Object[] a1, Object[] a2) {
        assertArrayEquals(a1, a2);
    }

    protected void eq(Object o1, Object o2) {
        assertEquals(o1, o2);
    }

    protected void ceq(Object o1, Object o2) {
        assertEquals(S.string(o1), S.string(o2));
    }

    protected void yes(Boolean expr, String msg, Object... args) {
        assertTrue(S.fmt(msg, args), expr);
    }

    protected void yes(Boolean expr) {
        assertTrue(expr);
    }

    protected void no(Boolean expr, String msg, Object... args) {
        assertFalse(S.fmt(msg, args), expr);
    }

    protected void no(Boolean expr) {
        assertFalse(expr);
    }

    protected void fail(String msg, Object... args) {
        assertFalse(S.fmt(msg, args), true);
    }

    protected static void run(Class<? extends ActTestBase> cls) {
        new JUnitCore().run(cls);
    }

    protected static void echo(String tmpl, Object... args) {
        System.out.println(String.format(tmpl, args));
    }

}
