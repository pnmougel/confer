

$("#btn_show_login").click( -> 
    $("#form_login").slideToggle("fast")
)

$("#logout").click( ->
    $.ajax(
        url: '/user/logout'
        type: 'GET'
        success: (data) ->
            message.addSuccess(data)
            $("#btn_show_logout").hide()
            $("#btn_show_login").show()
        error: (data) ->
            message.addError(data.responseText)
        complete: () ->
    )
)

$("#form_login_sign_in").click( ->
    $.ajax(
        url: '/user/login'
        type: 'POST'
        data: 
            email: $("#email").val()
            password: $("#password").val()
        success: (data) ->
            $("#form_login").slideToggle("fast")
            message.addSuccess(data)
            $("#btn_show_logout").show()
            $("#btn_show_login").hide()
        error: (data) ->
            message.addError(data.responseText)
        complete: () -> 
    )
)

$("#form_login_add_user").click( ->
    $.ajax(
        url: '/user/add'
        type: 'POST'
        data: 
            email: $("#email").val()
            password: $("#password").val()
        success: (data) ->
            $("#form_login").slideToggle("fast")
            message.addSuccess(data)
        error: (data) ->
            message.addError(data.responseText)
        complete: () -> 
    )
)

