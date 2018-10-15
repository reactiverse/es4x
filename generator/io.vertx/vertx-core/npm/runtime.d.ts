import {Vertx} from './index';

// The globally defined objects that are provided by the loader runtime
declare global {

  const vertx: Vertx;

  const require: {
    (id: string): any;
    resolve(): string;
    cache: any;
    extensions: any;
  };

  const process: {
    env: { [key: string]: string };
    pid: String;
    engine: String;
    exit: (exitCode: Number) => void;
    nextTick: (callback: (...args: any[]) => void) => void;
    stdout: any;
    stderr: any;
    stdin: any;
  };

  const Java: {
    /**
     * The type function loads the specified Java class and provides it as an object.
     *
     * Fields of this object can be read directly from it, and new instances can be created with the
     * JavaScript new keyword.
     *
     * @param className
     */
    type(className: string): any;

    /**
     * The from function creates a shallow copy of the Java datastructure (Array, List) as a JavaScript array.
     *
     * In many cases, this is not necessary, you can typically use the Java datastructure directly
     * from JavaScript.
     *
     * @param value
     */
    from(value: any): any;

    /**
     * The to function converts the argument to a Java dataype.
     *
     * When no toType is provided, Object[] is assumed.
     *
     * @param jsValue
     * @param javaType
     */
    to(jsValue: any, javaType: any): any;

    /**
     * The isJavaObject method returns whether obj is an object of the Java language.
     *
     * It returns false for native JavaScript objects, as well as for objects of other polyglot languages.
     *
     * @param obj
     */
    isJavaObject(obj: any): boolean;

    /**
     * The isType method returns whether obj is an object of the Java language,
     * representing a Java Class instance. It returns false for all other arguments.
     *
     * @param obj
     */
    isType(obj: any): boolean;

    /**
     * The typeName method returns the Java Class name of obj. obj is expected to represent
     * a Java Class instance, i.e., isType(obj) should return true; otherwise, undefined is returned.
     *
     * @param obj
     */
    typeName(obj: any): string | undefined;
  };

  function setTimeout(callback: (...args: any[]) => void, ms: number, ...args: any[]): Number;

  function clearTimeout(timeoutId: Number): any;

  function setInterval(callback: (...args: any[]) => void, ms: number, ...args: any[]): Number;

  function clearInterval(intervalId: Number): any;

  function setImmediate(callback: (...args: any[]) => void, ...args: any[]): any;
}
