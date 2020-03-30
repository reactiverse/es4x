/*
 * Copyright 2019 Paulo Lopes.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

// @ts-ignore
import {Vertx} from '@vertx/core';

declare var __filename: string;
declare var __dirname: string;

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
    cwd: () => string;
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

  /**
   * The Throwable class is the superclass of all errors and exceptions in the Java language.
   * Only objects that are instances of this class (or one of its subclasses) are thrown by
   * the Java Virtual Machine or can be thrown by the Java throw statement.
   *
   * Similarly, only this class or one of its subclasses can be the argument type in a catch clause.
   * For the purposes of compile-time checking of exceptions, Throwable and any subclass of Throwable
   * that is not also a subclass of either RuntimeException or Error are regarded as checked exceptions.
   */
  abstract class Throwable {
    /**
     * The constructor is disabled to avoid creation of Throwables from
     * the JavaScript side.
     */
    private constructor();

    /**
     * Prints this throwable and its backtrace to the standard error stream.
     */
    printStackTrace() : void;

    /**
     * Fills in the execution stack trace.
     */
    fillInStackTrace() : Throwable;

    /**
     * Returns the cause of this throwable or null if the cause is nonexistent or unknown.
     */
    getCause() : Throwable;

    /**
     * Returns the detail message string of this throwable.
     */
    getMessage() : String;

    /**
     * Returns an array containing all of the exceptions that were suppressed, typically by the
     * try-with-resources statement, in order to deliver this exception.
     */
    getSuppressed() : Throwable[];

    /**
     * Creates a localized description of this throwable.
     */
    getLocalizedMessage() : String;
  }
}

/**
 * The internal module for promisify functions or objects
 */
declare module "util" {
  export function promisify<R>(fn: (callback: (err: Error | null, result: R) => void) => void): () => PromiseLike<R>;

  export function promisify<R, P0>(arg0: P0, fn: (callback: (err: Error | null, result: R) => void) => void): (arg0: P0) => PromiseLike<R>;
  export function promisify<R, P0, P1>(arg0: P0, arg1: P1, fn: (callback: (err: Error | null, result: R) => void) => void): (arg0: P0, arg1: P1) => PromiseLike<R>;
  export function promisify<R, P0, P1, P2>(arg0: P0, arg1: P1, arg2: P2, fn: (callback: (err: Error | null, result: R) => void) => void): (arg0: P0, arg1: P1, arg2: P2) => PromiseLike<R>;
  export function promisify<R, P0, P1, P2, P3>(arg0: P0, arg1: P1, arg2: P2, arg3: P3, fn: (callback: (err: Error | null, result: R) => void) => void): (arg0: P0, arg1: P1, arg2: P2, arg3: P3) => PromiseLike<R>;
  export function promisify<R, P0, P1, P2, P3, P4>(arg0: P0, arg1: P1, arg2: P2, arg3: P3, arg4: P4,  fn: (callback: (err: Error | null, result: R) => void) => void): (arg0: P0, arg1: P1, arg2: P2, arg3: P3, arg4: P4) => PromiseLike<R>;
  export function promisify<R, P0, P1, P2, P3, P4, P5>(arg0: P0, arg1: P1, arg2: P2, arg3: P3, arg4: P4, arg5: P5,  fn: (callback: (err: Error | null, result: R) => void) => void): (arg0: P0, arg1: P1, arg2: P2, arg3: P3, arg4: P4, arg5: P5) => PromiseLike<R>;
  export function promisify<R, P0, P1, P2, P3, P4, P5, P6>(arg0: P0, arg1: P1, arg2: P2, arg3: P3, arg4: P4, arg5: P5, arg6: P6, fn: (callback: (err: Error | null, result: R) => void) => void): (arg0: P0, arg1: P1, arg2: P2, arg3: P3, arg4: P4, arg5: P5, arg6: P6) => PromiseLike<R>;

  export function promisify<TResult>(o: TResult): TResult;
}

/**
 * The internal async error module
 */
declare module "async-error" {
  export default function asyncError(error: Error | String | any): Error;
}
