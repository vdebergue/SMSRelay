'use strict'

angular.module('uiApp')
  .controller 'MainCtrl', ($scope, $http, $timeout, appConfig) ->
    baseUrl = appConfig.baseUrl
    conversationsKey = appConfig.conversationsKey
    registrationId = localStorage.getItem(appConfig.registrationKey)

    smsFeed = new EventSource(baseUrl + '/sms/feed/' + registrationId)

    $scope.sms = {}

    saveConversations = () ->
      localStorage.setItem(conversationsKey, JSON.stringify($scope.conversations))

    getConversations = () ->
      JSON.parse(localStorage.getItem(conversationsKey))

    $scope.conversations = getConversations() || []

    isSending = () ->
      $("#sendBtn").text("Sending ...")
      $("#sendBtn").attr("disabled", "disabled")
      $("#sendBtn").addClass("disabled")

    sent = () ->
      $("#sendBtn").text("Send")
      $("#sendBtn").removeAttr("disabled")
      $("#sendBtn").removeClass("disabled")

    $scope.send = () ->
      console.log($scope.sms)
      isSending()

      $http.post(baseUrl + '/sms/send/' + registrationId,
        {"text": $scope.sms.text, "phoneNumber": $scope.sms.phoneNumber}
      ).success((data, status, headers, config) ->
        $scope.sms.text = ""
        console.log(status)
        $timeout(sent, 1000)
      ).error((data, status, headers, config) ->
        console.log(data)
        $timeout(sent, 1000)
      )

    $scope.reply = (number) ->
      $scope.sms.phoneNumber = number
      $scope.sms.text = ""
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
      n = webkitNotifications.createNotification(
        'images/sms.jpg',
        'New Message From ' + message.sender.name,
        message.sms
      )
      n.onclick = (x) ->
        window.focus()
        this.cancel()
      n.show()

      setTimeout(() ->
        n.cancel()
      , 5000)

    smsFeed.addEventListener("message", (msg) ->
      message = JSON.parse(msg.data)
      if message.type == "smsFromPhone"
        $scope.$apply(() ->
          pushMessage(message)
        )
        notifyNewMessage(message)
        saveConversations()
    , false)

