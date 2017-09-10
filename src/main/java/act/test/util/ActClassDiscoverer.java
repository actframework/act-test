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
