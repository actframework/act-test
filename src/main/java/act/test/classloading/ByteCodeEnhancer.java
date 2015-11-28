package act.test.classloading;

import act.app.App;
import act.app.AppByteCodeScanner;
import act.controller.bytecode.ControllerEnhancer;
import act.mail.bytecode.MailerEnhancer;
import act.util.AppByteCodeEnhancer;
import act.util.AsmByteCodeEnhancer;
import org.osgl.$;

enum ByteCodeEnhancer {

    CONTROLLER(ControllerEnhancer.class),
    MAILER(MailerEnhancer.class);

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
