package com.nail.core.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import co.paralleluniverse.actors.behaviors.Server;
import co.paralleluniverse.actors.behaviors.ServerActor;
import co.paralleluniverse.common.util.Pair;
import co.paralleluniverse.concurrent.util.MapUtil;
import co.paralleluniverse.fibers.RuntimeSuspendExecution;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import com.google.common.collect.ImmutableSet;
import com.nail.utils.ClassHelper;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static net.bytebuddy.matcher.ElementMatchers.anyOf;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class ProxyActor extends ServerActor<ProxyActor.Invocation, Object, ProxyActor.Invocation> {
    private final Class<?>[] interfaces;
    private Object target;
    private final boolean callOnVoidMethods;

    private Map<String, Method> methodMap;

    /**
     * Creates a new {@code ProxyServerActor}
     *
     * @param name              the actor's name (may be null)
     * @param strand            the actor's strand (may be null)
     * @param mailboxConfig     this actor's mailbox settings.
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param target            the object implementing the actor's behaviors, on which the exposed interface methods will be called.
     * @param interfaces        the interfaces this actor's {@link ActorRef} will implement; {@code target} must implement all these interfaces.
     */
    public ProxyActor(String name, Strand strand, MailboxConfig mailboxConfig, boolean callOnVoidMethods, Object target, Class<?>[] interfaces) {
        super(name, null, 0L, null, strand, mailboxConfig);
        this.callOnVoidMethods = callOnVoidMethods;
        this.target = ActorLoader.getReplacementFor(target != null ? target : this);
        this.interfaces = interfaces != null ? Arrays.copyOf(interfaces, interfaces.length) : this.target.getClass().getInterfaces();
        if (this.interfaces == null)
            throw new IllegalArgumentException("No interfaces provided, and target of class " + this.target.getClass().getName() + " implements no interfaces");
        methodMap = ClassHelper.wrapInterfacesMthods(interfaces);
    }

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /////////// Constructors ///////////////////////////////////

    /**
     * Creates a new {@code ProxyServerActor}
     *
     * @param name              the actor's name (may be null)
     * @param mailboxConfig     this actor's mailbox settings.
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param target            the object implementing the actor's behaviors, on which the exposed interface methods will be called.
     * @param interfaces        the interfaces this actor's {@link ActorRef} will implement; {@code target} must implement all these interfaces.
     */
    public ProxyActor(String name, MailboxConfig mailboxConfig, boolean callOnVoidMethods, Object target, Class<?>... interfaces) {
        this(name, null, mailboxConfig, callOnVoidMethods, target, interfaces);
    }

    /**
     * Creates a new {@code ProxyServerActor} with the default mailbox settings.
     *
     * @param name              the actor's name (may be null)
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param target            the object implementing the actor's behaviors, on which the exposed interface methods will be called.
     * @param interfaces        the interfaces this actor's {@link ActorRef} will implement; {@code target} must implement all these interfaces.
     */
    public ProxyActor(String name, boolean callOnVoidMethods, Object target, Class<?>... interfaces) {
        this(name, null, null, callOnVoidMethods, target, interfaces);
    }

    /**
     * Creates a new {@code ProxyServerActor}
     *
     * @param mailboxConfig     this actor's mailbox settings.
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param target            the object implementing the actor's behaviors, on which the exposed interface methods will be called.
     * @param interfaces        the interfaces this actor's {@link ActorRef} will implement; {@code target} must implement all these interfaces.
     */
    public ProxyActor(MailboxConfig mailboxConfig, boolean callOnVoidMethods, Object target, Class<?>... interfaces) {
        this(null, null, mailboxConfig, callOnVoidMethods, target, interfaces);
    }

    /**
     * Creates a new {@code ProxyServerActor} with the default mailbox settings.
     *
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param target            the object implementing the actor's behaviors, on which the exposed interface methods will be called.
     * @param interfaces        the interfaces this actor's {@link ActorRef} will implement; {@code target} must implement all these interfaces.
     */
    public ProxyActor(boolean callOnVoidMethods, Object target, Class<?>... interfaces) {
        this(null, null, null, callOnVoidMethods, target, interfaces);
    }

    /**
     * Creates a new {@code ProxyServerActor}, which exposes all interfaces implemented by the given {@code target}.
     *
     * @param name              the actor's name (may be null)
     * @param mailboxConfig     this actor's mailbox settings.
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param target            the object implementing the actor's behaviors, on which the exposed interface methods will be called.
     */
    public ProxyActor(String name, MailboxConfig mailboxConfig, boolean callOnVoidMethods, Object target) {
        this(name, null, mailboxConfig, callOnVoidMethods, target, null);
    }

    /**
     * Creates a new {@code ProxyServerActor} with the default mailbox settings,
     * which exposes all interfaces implemented by the given {@code target}.
     *
     * @param name              the actor's name (may be null)
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param target            the object implementing the actor's behaviors, on which the exposed interface methods will be called.
     */
    public ProxyActor(String name, boolean callOnVoidMethods, Object target) {
        this(name, null, null, callOnVoidMethods, target, null);
    }

    /**
     * Creates a new {@code ProxyServerActor}, which exposes all interfaces implemented by the given {@code target}.
     *
     * @param mailboxConfig     this actor's mailbox settings.
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param target            the object implementing the actor's behaviors, on which the exposed interface methods will be called.
     */
    public ProxyActor(MailboxConfig mailboxConfig, boolean callOnVoidMethods, Object target) {
        this(null, null, mailboxConfig, callOnVoidMethods, target, null);
    }

    /**
     * Creates a new {@code ProxyServerActor} with the default mailbox settings,
     * which exposes all interfaces implemented by the given {@code target}.
     *
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param target            the object implementing the actor's behaviors, on which the exposed interface methods will be called.
     */
    public ProxyActor(boolean callOnVoidMethods, Object target) {
        this(null, null, null, callOnVoidMethods, target, null);
    }

    /**
     * This constructor is for use by subclasses that are intended to serve as the target. This object will serve as the target
     * for the method calls.
     *
     * @param name              the actor's name (may be null)
     * @param mailboxConfig     this actor's mailbox settings.
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param interfaces        the interfaces this actor's {@link ActorRef} will implement; this class must implement all these interfaces.
     */
    protected ProxyActor(String name, MailboxConfig mailboxConfig, boolean callOnVoidMethods, Class<?>... interfaces) {
        this(name, null, mailboxConfig, callOnVoidMethods, null, interfaces);
    }

    /**
     * This constructor is for use by subclasses that are intended to serve as the target. This object will serve as the target
     * for the method calls. The default mailbox settings will be used.
     *
     * @param name              the actor's name (may be null)
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param interfaces        the interfaces this actor's {@link ActorRef} will implement; this class must implement all these interfaces.
     */
    protected ProxyActor(String name, boolean callOnVoidMethods, Class<?>... interfaces) {
        this(name, null, null, callOnVoidMethods, null, interfaces);
    }

    /**
     * This constructor is for use by subclasses that are intended to serve as the target. This object will serve as the target
     * for the method calls. The default mailbox settings will be used.
     *
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param interfaces        the interfaces this actor's {@link ActorRef} will implement; this class must implement all these interfaces.
     */
    protected ProxyActor(MailboxConfig mailboxConfig, boolean callOnVoidMethods, Class<?>... interfaces) {
        this(null, null, mailboxConfig, callOnVoidMethods, null, interfaces);
    }

    /**
     * This constructor is for use by subclasses that are intended to serve as the target. This object will serve as the target
     * for the method calls. The default mailbox settings will be used.
     *
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     * @param interfaces        the interfaces this actor's {@link ActorRef} will implement; this class must implement all these interfaces.
     */
    protected ProxyActor(boolean callOnVoidMethods, Class<?>... interfaces) {
        this(null, null, null, callOnVoidMethods, null, interfaces);
    }

    /**
     * This constructor is for use by subclasses that are intended to serve as the target. This object will serve as the target
     * for the method calls, and all of the interfaces implemented by the subclass will be exposed by the {@link ActorRef}.
     *
     * @param name              the actor's name (may be null)
     * @param mailboxConfig     this actor's mailbox settings.
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     */
    protected ProxyActor(String name, MailboxConfig mailboxConfig, boolean callOnVoidMethods) {
        this(name, null, mailboxConfig, callOnVoidMethods, null, null);
    }

    /**
     * This constructor is for use by subclasses that are intended to serve as the target. This object will serve as the target
     * for the method calls, and all of the interfaces implemented by the subclass will be exposed by the {@link ActorRef}.
     * The default mailbox settings will be used.
     *
     * @param name              the actor's name (may be null)
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     */
    protected ProxyActor(String name, boolean callOnVoidMethods) {
        this(name, null, null, callOnVoidMethods, null, null);
    }

    /**
     * This constructor is for use by subclasses that are intended to serve as the target. This object will serve as the target
     * for the method calls, and all of the interfaces implemented by the subclass will be exposed by the {@link ActorRef}.
     *
     * @param mailboxConfig     this actor's mailbox settings.
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     */
    protected ProxyActor(MailboxConfig mailboxConfig, boolean callOnVoidMethods) {
        this(null, null, mailboxConfig, callOnVoidMethods, null, null);
    }

    /**
     * This constructor is for use by subclasses that are intended to serve as the target. This object will serve as the target
     * for the method calls, and all of the interfaces implemented by the subclass will be exposed by the {@link ActorRef}.
     * The default mailbox settings will be used.
     *
     * @param callOnVoidMethods whether calling void methods will block until they have completed execution
     */
    protected ProxyActor(boolean callOnVoidMethods) {
        this(null, null, null, callOnVoidMethods, null, null);
    }
    //</editor-fold>

    @Override
    protected final Server<ProxyActor.Invocation, Object, ProxyActor.Invocation> makeRef(ActorRef<Object> ref) {
        try {
            return getProxyClass(interfaces, callOnVoidMethods)
                    .getConstructor(ActorRef.class, InvocationHandler.class, Map.class)
                    .newInstance(ref, (callOnVoidMethods ? handler1 : handler2), methodMap);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static final ConcurrentMap<Pair<Set<Class<?>>, Boolean>, Class<? extends Server>> classes = MapUtil.newConcurrentHashMap();
    private static final ProxyActor.ObjectProxyServerImpl handler1 = new ProxyActor.ObjectProxyServerImpl(true);
    private static final ProxyActor.ObjectProxyServerImpl handler2 = new ProxyActor.ObjectProxyServerImpl(false);

    private static Class<? extends Server> getProxyClass(Class<?>[] interfaces, boolean callOnVoidMethods) {
        final Pair<Set<Class<?>>, Boolean> key = new Pair(ImmutableSet.copyOf(interfaces), callOnVoidMethods);
        Class<? extends Server> clazz = classes.get(key);
        if (clazz == null) {
            clazz = new ByteBuddy() // http://bytebuddy.net/
                    .subclass(ProxyServer.class)
                    .implement(interfaces)
                    .implement(java.io.Serializable.class)
                    .method(isDeclaredBy(anyOf(interfaces))).intercept(InvocationHandlerAdapter.of(callOnVoidMethods ? handler1 : handler2))
                    .make()
                    .load(ProxyServerActor.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
            final Class<? extends Server> old = classes.putIfAbsent(key, clazz);
            if (old != null)
                clazz = old;
        }
        return clazz;
    }

    private static class ObjectProxyServerImpl implements InvocationHandler, java.io.Serializable {
        private final boolean callOnVoidMethods;

        private ObjectProxyServerImpl(boolean callOnVoidMethods) {
            this.callOnVoidMethods = callOnVoidMethods;
        }

        boolean isInActor(Server<ProxyActor.Invocation, Object, ProxyActor.Invocation> ref) {
            return Objects.equals(ref, LocalActor.self());
        }

        @Override
        @Suspendable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            assert !method.getDeclaringClass().isAssignableFrom(ActorRefDelegate.class);

            assert !method.getDeclaringClass().isAssignableFrom(Server.class);
//            final Class<?> cls = method.getDeclaringClass();
//            if (cls.isAssignableFrom(Server.class) || cls.isAssignableFrom(SendPort.class)) {
//                try {
//                    return method.invoke(ref, args);
//                } catch (InvocationTargetException e) {
//                    throw e.getCause();
//                }
//            }

            final Server<ProxyActor.Invocation, Object, ProxyActor.Invocation> ref = (Server<ProxyActor.Invocation, Object, ProxyActor.Invocation>) proxy;
            try {
                if (isInActor(ref)) {
                    try {
                        return method.invoke(ServerActor.currentServerActor(), args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                } else {
                    final ProxyActor.Invocation m = new ProxyActor.Invocation(method, args, false);
                    if (callOnVoidMethods || (method.getReturnType() != void.class && method.getReturnType() != Void.class))
                        return ref.call(m);
                    else {
                        ref.cast(m);
                        return null;
                    }
                }
            } catch (SuspendExecution e) {
                throw RuntimeSuspendExecution.of(e);
            }
        }

        protected Object readResolve() throws java.io.ObjectStreamException {
            return callOnVoidMethods ? handler1 : handler2;
        }
    }

    @Override
    protected void checkCodeSwap() throws SuspendExecution {
        verifyInActor();
        Object _target = ActorLoader.getReplacementFor(target);
        if (_target != target)
            log().info("Upgraded ProxyServerActor implementation: {}", _target);
        this.target = _target;
        super.checkCodeSwap();
    }

    @Override
    protected Object handleCall(ActorRef<?> from, Object id, ProxyActor.Invocation m) throws Exception, SuspendExecution {
        try {
            Object res = m.invoke(target);
            return res == null ? NULL_RETURN_VALUE : res;
        } catch (InvocationTargetException e) {
            assert !(e.getCause() instanceof SuspendExecution);
            log().error("handleCall: Invocation " + m + " has thrown an exception.", e.getCause());
            throw rethrow(e.getCause());
        }
    }

    @Override
    protected void handleCast(ActorRef<?> from, Object id, ProxyActor.Invocation m) throws SuspendExecution {
        try {
            m.invoke(target);
        } catch (InvocationTargetException e) {
            assert !(e.getCause() instanceof SuspendExecution);
            log().error("handleCast: Invocation " + m + " has thrown an exception.", e.getCause());
        }
    }

    protected static class Invocation implements java.io.Serializable {
        private final Method method;
        private final Object[] params;

        public Invocation(Method method, List<Object> params) {
            this.method = method;
            this.params = params.toArray(new Object[params.size()]);
        }

        public Invocation(Method method, Object... params) {
            this(method, params, false);
        }

        Invocation(Method method, Object[] params, boolean copy) {
            this.method = method;
            this.params = copy ? Arrays.copyOf(params, params.length) : params;
        }

        public Method getMethod() {
            return method;
        }

        public List<Object> getParams() {
            return Collections.unmodifiableList(Arrays.asList(params));
        }

        Object invoke(Object target) throws SuspendExecution, InvocationTargetException {
            try {
                return method.invoke(target, params);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }

        @Override
        public String toString() {
            return method.toString() + Arrays.toString(params);
        }
    }

    private static RuntimeException rethrow(Throwable t) throws Exception {
        if (t instanceof Exception)
            throw (Exception) t;
        if (t instanceof Error)
            throw (Error) t;
        throw new RuntimeException(t);
    }
}
