'use strict'

angular.module('uiApp', [
  'ngCookies',
  'ngResource',
  'ngSanitize'
])
  .config ($routeProvider, $locationProvider) ->
    $routeProvider
      .when '/',
        templateUrl: 'views/main.html'
        controller: 'MainCtrl'
      .when '/settings',
        templateUrl: 'views/settings.html'
        controller: 'SettingsCtrl'
      .when '/login',
        templateUrl: 'views/login.html'
        controller: 'LoginCtrl'
      .otherwise
        redirectTo: '/'

    #$locationProvider.html5Mode(true)
  .filter('nl2br', () ->
    (text) ->
      text.replace(/\n/g, '<br/>')
  ).constant("appConfig",
    baseUrl: "/smsrelay"
    registrationKey: "registrationId"
    conversationsKey: "conversations"
  ).run( ($rootScope, $location) ->
    $rootScope.$on("$routeChangeStart", (event, next, current)->
      if not localStorage.getItem("registrationId")
        # not logged in
        if next.templateUrl != 'views/login.html'
          $location.path("/login")
    )
  )
