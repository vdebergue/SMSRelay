'use strict'

angular.module('uiApp')
  .controller 'LoginCtrl', ($scope, $http, $location, appConfig) ->

    $scope.form = {}

    baseUrl = appConfig.baseUrl

    showError = () ->
      $(".form-group").addClass("has-error")

    clearError = () ->
      $(".form-group").removeClass("has-error")      

    $scope.login = () ->
      clearError()
      $http.post(baseUrl + '/login',
        "login": $scope.form.login
        "password": $scope.form.password
      ).success((data, status, headers, config) ->
        # get registrationId, storeIt in localStorage
        localStorage.setItem(appConfig.registrationKey, data.registrationId)

        # redirect to MainCtrl
        $location.path("/")
      ).error((data, status, headers, config) ->
        # display error
        showError()
      )

