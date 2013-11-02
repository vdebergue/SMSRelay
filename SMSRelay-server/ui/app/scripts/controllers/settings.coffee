'use strict'

angular.module('uiApp')
  .controller 'SettingsCtrl', ($scope) ->

    $("#notif").on("click", () ->
      webkitNotifications.requestPermission()
    )

