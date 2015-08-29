package act.test.util;

import act.test.util.ActClass;

class ActClassDiscoverer {
    /**
     * @param annotatedClass class on which to look for {@code ActClass} annotation
     * @return values specified on the {@code ActClass} annotation
     */
    public static String[] getActClassNamePatternsOn(Class<?> annotatedClass) {
        ActClass annotation = annotatedClass.getAnnotation(ActClass.class);

        if (annotation != null) {
            return annotation.value();
        }

        return new String[]{};
    }
}
