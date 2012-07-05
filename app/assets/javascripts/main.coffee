

window.message = {
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

jQuery(document).ready( ->
    jQuery("time.timeago").timeago()
)

$("#btn_show_new_conf").click( -> $("#form_new_conf").toggle("fast"))
