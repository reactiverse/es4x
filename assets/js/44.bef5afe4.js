(window.webpackJsonp=window.webpackJsonp||[]).push([[44],{410:function(t,a,e){"use strict";e.r(a);var s=e(42),n=Object(s.a)({},(function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("ContentSlotsDistributor",{attrs:{"slot-key":t.$parent.slotKey}},[e("h1",{attrs:{id:"logging"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#logging"}},[t._v("#")]),t._v(" Logging")]),t._v(" "),e("p",[t._v("Η καταγραφή (logging) είναι ένα πολύ κοινό χαρακτηριστικό για όλες τις εφαρμογές. Το ES4X δεν εφαρμόζει καταγραφικό, αλλά χρησιμοποιεί το καταγραφικό από το JDK, επίσης γνωστός ως "),e("em",[t._v("java util logging")]),t._v(" ή "),e("em",[t._v("JUL")]),t._v(". Ακόμα και το "),e("code",[t._v("console")]),t._v(" αντικείμενο είναι συνδεδεμένο με αυτό, οπότε μπορείτε να απενεργοποιήσετε την κονσόλα από την καταγραφή σε ένα συγκεκριμένο επίπεδο κατά το χρόνο εκτέλεσης, χρησιμοποιώντας τη διαμόρφωση (configuration).")]),t._v(" "),e("p",[t._v("Για να προσαρμόσετε την καταγραφή, δημιουργήστε ένα αρχείο "),e("code",[t._v("logging.properties")]),t._v(" με τη διαμόρφωση που θέλετε. Η προεπιλεγμένη διαμόρφωση είναι:")]),t._v(" "),e("div",{staticClass:"language-properties extra-class"},[e("pre",{pre:!0,attrs:{class:"language-properties"}},[e("code",[e("span",{pre:!0,attrs:{class:"token attr-name"}},[t._v("handlers")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("=")]),e("span",{pre:!0,attrs:{class:"token attr-value"}},[t._v("java.util.logging.ConsoleHandler")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token attr-name"}},[t._v("java.util.logging.ConsoleHandler.formatter")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("=")]),e("span",{pre:!0,attrs:{class:"token attr-value"}},[t._v("io.reactiverse.es4x.jul.ES4XFormatter")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token attr-name"}},[t._v("java.util.logging.ConsoleHandler.level")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("=")]),e("span",{pre:!0,attrs:{class:"token attr-value"}},[t._v("FINEST")]),t._v("\n\n"),e("span",{pre:!0,attrs:{class:"token attr-name"}},[t._v(".level")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("=")]),e("span",{pre:!0,attrs:{class:"token attr-value"}},[t._v("INFO")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token attr-name"}},[t._v("io.reactiverse.level")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("=")]),e("span",{pre:!0,attrs:{class:"token attr-value"}},[t._v("INFO")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token attr-name"}},[t._v("io.vertx.level")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("=")]),e("span",{pre:!0,attrs:{class:"token attr-value"}},[t._v("INFO")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token attr-name"}},[t._v("com.hazelcast.level")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("=")]),e("span",{pre:!0,attrs:{class:"token attr-value"}},[t._v("INFO")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token attr-name"}},[t._v("io.netty.util.internal.PlatformDependent.level")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("=")]),e("span",{pre:!0,attrs:{class:"token attr-value"}},[t._v("SEVERE")]),t._v("\n")])])]),e("p",[t._v("Μπορείτε να δείτε ότι υπάρχει ένα προσαρμοσμένο μορφοποιητή, αυτό είναι για να σας προσφέρει "),e("code",[t._v("ANSI")]),t._v(" χρωματιστό καταγραφικό. Εάν ο μορφοποιητής είναι απενεργοποιημένος, όλα τα αρχεία καταγραφής θα είναι σε απλό κείμενο χωρίς "),e("code",[t._v("ANSI")]),t._v(" κωδικούς.")]),t._v(" "),e("p",[t._v("::: υπόδειξη\nΓια καταγραφή προειδοποίησης και σφαλμάτων κατά το χρόνο εκτέλεσης, συμπεριλαμβανομένου του "),e("code",[t._v("console")]),t._v(" αντικειμένου, απλά ανεβάστε το επίπεδο στο χειριστή της κονσόλας.")]),t._v(" "),e("p",[t._v("Μπορείτε ακόμη και να στείλετε τα αρχεία καταγραφής σε άλλες τοποθεσίες προσθέτοντας περισσότερους χειριστές.\n:::")])])}),[],!1,null,null,null);a.default=n.exports}}]);