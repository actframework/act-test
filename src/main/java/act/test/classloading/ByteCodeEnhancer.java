package act.test.classloading;

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

import act.app.App;
import act.controller.bytecode.ControllerEnhancer;
import act.db.EntityClassEnhancer;
import act.mail.bytecode.MailerEnhancer;
import act.util.AppByteCodeEnhancer;
import act.util.AsmByteCodeEnhancer;
import act.util.DataObjectEnhancer;
import act.util.SingletonEnhancer;
import org.osgl.$;

enum ByteCodeEnhancer {

    CONTROLLER(ControllerEnhancer.class),
    DATA_OBJECT(DataObjectEnhancer.class),
    DB_ENTITY_CLASS(EntityClassEnhancer.class),
    MAILER(MailerEnhancer.class),
    SINGLETON(SingletonEnhancer.class);

    private Class<? extends AsmByteCodeEnhancer> enhancer;

    private ByteCodeEnhancer(Class<? extends AsmByteCodeEnhancer> enhancer) {
        this.enhancer = $.notNull(enhancer);
    }

    public AsmByteCodeEnhancer enhancer(App app) {
        AsmByteCodeEnhancer e = $.newInstance(enhancer);
        if (e instanceof AppByteCodeEnhancer) {
            ((AppByteCodeEnhancer) e).app(app);
        }
        return e;
    }

}
