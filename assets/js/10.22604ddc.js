(window.webpackJsonp=window.webpackJsonp||[]).push([[10],{278:function(t,a,s){t.exports=s.p+"assets/img/debug.a2318191.png"},279:function(t,a,s){t.exports=s.p+"assets/img/vscode-debug.8f0f75df.png"},344:function(t,a,s){"use strict";s.r(a);var e=s(14),r=Object(e.a)({},(function(){var t=this,a=t._self._c;return a("ContentSlotsDistributor",{attrs:{"slot-key":t.$parent.slotKey}},[a("h1",{attrs:{id:"debug"}},[a("a",{staticClass:"header-anchor",attrs:{href:"#debug"}},[t._v("#")]),t._v(" Debug")]),t._v(" "),a("h2",{attrs:{id:"chrome-inspector"}},[a("a",{staticClass:"header-anchor",attrs:{href:"#chrome-inspector"}},[t._v("#")]),t._v(" Chrome Inspector")]),t._v(" "),a("p",[t._v("When working on "),a("a",{attrs:{href:"https://graalvm.org",target:"_blank",rel:"noopener noreferrer"}},[t._v("GraalVM"),a("OutboundLink")],1),t._v(" or a JDK with the graalvm (JVMCI) bits, start your application as:")]),t._v(" "),a("div",{staticClass:"language-sh extra-class"},[a("pre",{pre:!0,attrs:{class:"language-sh"}},[a("code",[a("span",{pre:!0,attrs:{class:"token function"}},[t._v("npm")]),t._v(" start -- "),a("span",{pre:!0,attrs:{class:"token parameter variable"}},[t._v("-Dinspect")]),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),a("span",{pre:!0,attrs:{class:"token number"}},[t._v("9229")]),t._v("\n")])])]),a("p",[t._v("This will start a Chrome inspector debugger agent on port 9229 that you can attach for a remote\ndebug session from your Browser.")]),t._v(" "),a("div",{staticClass:"language- extra-class"},[a("pre",{pre:!0,attrs:{class:"language-text"}},[a("code",[t._v("Chrome devtools listening at port: 9229\nRunning: java ...\nDebugger listening on port 9229.\nTo start debugging, open the following URL in Chrome:\n    chrome-devtools://devtools/bundled/js_app.html?ws=127.0.0.1:9229/436e852b-329b5c44c3e\nServer listening at: http://localhost:8080/\n")])])]),a("p",[a("img",{attrs:{src:s(278),alt:"chrome-inspector"}})]),t._v(" "),a("p",[t._v("You will be able to set breakpoints, debug etc...")]),t._v(" "),a("h2",{attrs:{id:"debug-from-vscode"}},[a("a",{staticClass:"header-anchor",attrs:{href:"#debug-from-vscode"}},[t._v("#")]),t._v(" Debug from VSCode")]),t._v(" "),a("p",[t._v("The usage of Chrome devtools is not a hard requirement. You can also debug the application using\n"),a("a",{attrs:{href:"https://code.visualstudio.com",target:"_blank",rel:"noopener noreferrer"}},[t._v("Visual Studio Code"),a("OutboundLink")],1),t._v(".")]),t._v(" "),a("div",{staticClass:"custom-block warning"},[a("p",{staticClass:"custom-block-title"},[t._v("WARNING")]),t._v(" "),a("p",[t._v("Before you can debug from the IDE, you need to install the extension: "),a("a",{attrs:{href:"https://marketplace.visualstudio.com/items?itemName=oracle-labs-graalvm.graalvm",target:"_blank",rel:"noopener noreferrer"}},[t._v("GraalVM Tools for Java"),a("OutboundLink")],1),t._v(" and create a runner configuration.")]),t._v(" "),a("p",[t._v("To create the attach configuration either use the helper command, or use the template bellow:")]),t._v(" "),a("div",{staticClass:"language- extra-class"},[a("pre",{pre:!0,attrs:{class:"language-text"}},[a("code",[t._v("es4x vscode\n")])])]),a("p",[t._v("This will create a "),a("code",[t._v("launcher.json")]),t._v(" similar to this:")]),t._v(" "),a("div",{staticClass:"language-json extra-class"},[a("pre",{pre:!0,attrs:{class:"language-json"}},[a("code",[a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),t._v("\n  "),a("span",{pre:!0,attrs:{class:"token property"}},[t._v('"version"')]),t._v(" "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v(":")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token string"}},[t._v('"0.2.0"')]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v("\n  "),a("span",{pre:!0,attrs:{class:"token property"}},[t._v('"configurations"')]),t._v(" "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v(":")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("[")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),t._v("\n    "),a("span",{pre:!0,attrs:{class:"token property"}},[t._v('"name"')]),t._v(" "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v(":")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token string"}},[t._v('"Launch empty-project"')]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v("\n    "),a("span",{pre:!0,attrs:{class:"token property"}},[t._v('"type"')]),t._v(" "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v(":")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token string"}},[t._v('"graalvm"')]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v("\n    "),a("span",{pre:!0,attrs:{class:"token property"}},[t._v('"request"')]),t._v(" "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v(":")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token string"}},[t._v('"attach"')]),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v("\n    "),a("span",{pre:!0,attrs:{class:"token property"}},[t._v('"port"')]),t._v(" "),a("span",{pre:!0,attrs:{class:"token operator"}},[t._v(":")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token number"}},[t._v("9229")]),t._v("\n  "),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v(" "),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("]")]),t._v("\n"),a("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v("\n")])])]),a("p",[t._v("And attach your debugger.")]),t._v(" "),a("p",[a("img",{attrs:{src:s(279),alt:"vscode-chrome-inspector"}})])])])}),[],!1,null,null,null);a.default=r.exports}}]);