'use strict'

angular.module('uiApp')
  .controller 'MainCtrl', ($scope, $http) ->
    baseUrl = ''
    conversationsKey = "conversations"

    smsFeed = new EventSource(baseUrl + '/sms/feed')

    $scope.sms = {}

    saveConversations = () ->
      localStorage.setItem(conversationsKey, JSON.stringify($scope.conversations))

    getConversations = () ->
      JSON.parse(localStorage.getItem(conversationsKey))

    $scope.conversations = getConversations() || []

    $scope.send = () ->
      console.log($scope.sms)
      $http.post(baseUrl + '/sms/send',
        {"text": $scope.sms.text, "phoneNumber": $scope.sms.phoneNumber}
      ).success((data, status, headers, config) ->
        console.log(status)
      )

    $scope.reply = (number) ->
      $scope.sms.phoneNumber = number
      $("textarea").value = ""
      setTimeout(() ->
        $("textarea").focus()
      , 1)


  # "sender" : {
  #   "number" : "+33671002252",
  #   "name" : "Vincent Debergue"
  # },
  # "sms" : "Test from browser",
    pushMessage = (message) ->
      console.log(message)
      found = false
      for conv in $scope.conversations
        if conv.number == message.sender.number
          conv.messages.push("text": message.sms, "date": message.date)
          conv.lastMessage = message.date
          found = true
          break

      if not found
        $scope.conversations.push(
          "name" : message.sender.name
          "number" : message.sender.number
          "messages" : [
            "text": message.sms
            "date": message.date
          ]
          "lastMessage": message.date
        )

    notifyNewMessage = (message) ->
      webkitNotifications.createNotification(
        'images/sms.jpg',
        'New Message From ' + message.sender.name,
        message.sms
      ).show()

    smsFeed.addEventListener("message", (msg) ->
      message = JSON.parse(msg.data)
      if message.type == "smsFromPhone"
        $scope.$apply(() ->
          pushMessage(message)
        )
        notifyNewMessage(message)
        saveConversations()
    , false)

