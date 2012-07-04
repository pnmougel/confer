

message = {
    addMessage : (message, panel = "#messageAreaTop", timed = true, type, strong) ->
        $(panel).append("""<div class="alert #{type}">
            <button class="close" data-dismiss="alert">×</button>
            <strong>#{strong}</strong> #{message}
        </div>""")
        # $("div.alert").delay(1000).slideToggle("slow", -> )
        $("div.alert").delay(5000).hide("clip")
    addInfo : (message, panel = "#messageAreaTop", timed = true) ->
        this.addMessage(message, panel, timed, "alert-info", "Info:")
    addSuccess : (message, panel = "#messageAreaTop", timed = true) ->
        this.addMessage(message, panel, timed, "alert-success", "Success:")
    addWarning : (message, panel = "#messageAreaTop", timed = true) ->
        this.addMessage(message, panel, timed, "", "Warning:")
    addError : (message, panel = "#messageAreaTop", timed = true) ->
        this.addMessage(message, panel, timed, "alert-error", "Error:")
}

$("#btn_show_new_conf").click( -> $("#form_new_conf").toggle("fast"))

$("#btn_show_new_url").click( -> 
    $(".add_url_form").slideToggle("fast")
)

$("#btn_show_new_comment").click( -> 
    $("#add_comment_form").slideToggle("fast")
)


$("#btn_show_login").click( -> 
    $("#form_login").slideToggle("fast")
)

$("#submit_add_url").click( ->
    $.ajax(
        url: '/link'
        type: 'POST'
        data: 
            url: $("#input_form_add_url").val()
            conference_id: $("#conference_id").val()
        success: (data) ->
            $('#confLinks').append(data)
            $(".add_url_form").slideToggle("fast")
        error: (data) -> 
            message.addError(data.responseText)
        complete: (data, x, e) -> 
    )
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

window.deleteUrl = (id) -> 
    $.ajax({
        type: 'DELETE',
        url: '/link/' + id
    });
    $('#link_' + id).hide("slow");



        
