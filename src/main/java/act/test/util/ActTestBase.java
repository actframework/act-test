package act.test.util;

/*-
 * #%L
 * ACT TEST
 * %%
 * Copyright (C) 2015 - 2017 ActFramework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import act.app.ActionContext;
import act.app.App;
import act.app.AppClassLoader;
import act.conf.AppConfig;
import act.event.EventBus;
import act.job.AppJobManager;
import act.route.Router;
import act.view.RenderAny;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgl.$;
import org.osgl.cache.CacheService;
import org.osgl.http.H;
import org.osgl.mvc.result.Result;
import org.osgl.util.IO;
import org.osgl.util.S;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@Ignore
@RunWith(ActTestRunner.class)
public class ActTestBase extends Assert {

    protected App app;
    protected AppClassLoader appClassLoader;
    protected CacheService cache;
    protected Router router;
    protected ActionContext actionContext;
    protected AppConfig appConfig;
    protected AppJobManager jobManager;
    protected H.Session session;
    protected H.Request request;
    protected H.Response response;
    protected EventBus eventBus;
    protected ByteArrayOutputStream baos;

    @Before
    public void _before() throws Exception {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        app = mock(App.class);
        appClassLoader = mock(AppClassLoader.class);
        cache = mock(CacheService.class);
        request = mock(H.Request.class);
        response = mock(H.Response.class);
        session = new H.Session();
        jobManager = mock(AppJobManager.class);
        eventBus = mock(EventBus.class);
        appConfig = mock(AppConfig.class);
        router = mock(Router.class);
        Field f = App.class.getDeclaredField("INST");
        f.setAccessible(true);
        f.set(null, app);
        when(app.cache()).thenReturn(cache);
        when(app.classLoader()).thenReturn(appClassLoader);
        when(app.jobManager()).thenReturn(jobManager);
        when(app.eventBus()).thenReturn(eventBus);
        when(app.config()).thenReturn(appConfig);
        when(app.router()).thenReturn(router);
        when(app.getInstance(any(Class.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return $.newInstance((Class) args[0]);
            }
        });
        when(appConfig.corsEnabled()).thenReturn(false);
        when(request.method()).thenReturn(H.Method.GET);
        actionContext = spy(ActionContext.create(app, request, response));
        doReturn(session).when(actionContext).session();
        baos = new ByteArrayOutputStream();
        when(response.outputStream()).thenReturn(baos);
        when(response.writeContent(anyString())).thenAnswer(new Answer<H.Response>() {
            @Override
            public H.Response answer(InvocationOnMock invocationOnMock) throws Throwable {
                String s = (String) invocationOnMock.getArguments()[0];
                IO.writeContent(s, new OutputStreamWriter(baos));
                return response;
            }
        });
        setup();
    }

    protected void applyResult(Result result) {
        if (result instanceof RenderAny) {
            RenderAny any = $.cast(result);
            any.apply(actionContext);
        } else {
            result.apply(request, response);
        }
    }

    protected void setup() throws Exception {}

    protected byte[] responseAsByteArray() {
        return baos.toByteArray();
    }

    protected String responseAsString() {
        return new String(baos.toByteArray());
    }

    protected JSONObject responseAsJSONObject() {
        return JSON.parseObject(responseAsString());
    }

    protected JSONArray responseAsJSONArray() {
        return JSON.parseArray(responseAsString());
    }

    protected static void isNull(Object a) {
        assertNull(a);
    }

    protected static void notNull(Object a) {
        assertNotNull(a);
    }

    protected static void same(Object a, Object b) {
        assertSame(a, b);
    }

    protected static void eq(Object[] a1, Object[] a2) {
        assertArrayEquals(a1, a2);
    }

    protected static void eq(Object o1, Object o2) {
        assertEquals(o1, o2);
    }

    protected static void ne(Object o1, Object o2) {
        assertNotEquals(o1, o2);
    }

    protected static void ceq(Object o1, Object o2) {
        assertEquals(S.string(o1), S.string(o2));
    }

    protected static void yes(Boolean expr, String msg, Object... args) {
        assertTrue(S.fmt(msg, args), expr);
    }

    protected static void yes(Boolean expr) {
        assertTrue(expr);
    }

    protected static void no(Boolean expr, String msg, Object... args) {
        assertFalse(S.fmt(msg, args), expr);
    }

    protected static void no(Boolean expr) {
        assertFalse(expr);
    }

    protected static void fail(String msg, Object... args) {
        assertFalse(S.fmt(msg, args), true);
    }

    protected static void run(Class<? extends ActTestBase> cls) {
        new JUnitCore().run(cls);
    }

    protected static void echo(String tmpl, Object... args) {
        System.out.println(String.format(tmpl, args));
    }


}
