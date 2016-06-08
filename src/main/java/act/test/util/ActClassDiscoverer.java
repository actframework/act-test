package act.test.util;

import org.osgl.util.C;

import java.util.List;

class ActClassDiscoverer {
    /**
     * @param annotatedClass class on which to look for {@code ActClass} annotation
     * @return values specified on the {@code ActClass} annotation
     */
    public static List<String> getActClassNamePatternsOn(Class<?> annotatedClass) {
        C.List<String> l = C.newList();

        Class<?> parent = annotatedClass.getSuperclass();
        if (null != parent) {
            l.addAll(getActClassNamePatternsOn(parent));
        }

        ActClass annotation = annotatedClass.getAnnotation(ActClass.class);
        if (annotation != null) {
            l.addAll(C.listOf(annotation.value()));
        }

        return l;
    }
}
