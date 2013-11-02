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
      .otherwise
        redirectTo: '/'

    #$locationProvider.html5Mode(true)
