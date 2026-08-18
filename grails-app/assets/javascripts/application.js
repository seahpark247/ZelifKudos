// Manifest for application.js.
//
// jQuery and Bootstrap used to be required here. They came to 256KB on every
// page and powered exactly one thing — an ajaxStart/ajaxStop spinner — which
// never fired, because nothing in this app uses jQuery.ajax. The app's own
// scripts are vanilla.
//
//= require_self
