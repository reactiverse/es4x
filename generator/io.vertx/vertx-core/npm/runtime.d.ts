import { Vertx } from './index';

declare function setTimeout(callback: (...args: any[]) => void, ms: number, ...args: any[]): Number;
declare function clearTimeout(timeoutId: Number);
declare function setInterval(callback: (...args: any[]) => void, ms: number, ...args: any[]): Number;
declare function clearInterval(intervalId: Number);
declare function setImmediate(callback: (...args: any[]) => void, ...args: any[]): any;

declare const require: {
  (id: string): any;
  resolve(): string;
  cache: any;
  extensions: any;
};

declare const Java: {
  extend(type: any, impl?: object): any
  super(variable: object): any
  from(value: any): any
  to(jsValue: any, javaType: any): any
  type(className: string): any
};

declare const process: {
  env: Map<String, String>;
  pid: String;
  exit: (exitCode: Number) => void;
  nextTick: (callback: (...args: any[]) => void) => void;
  stdout: any;
  stderr: any;
  stdin: any;
};

// The globally defined objects that are provided by the loader runtime
declare global {
  const vertx : Vertx;
}
