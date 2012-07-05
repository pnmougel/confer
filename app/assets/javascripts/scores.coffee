
$("#show_vote_form").click( -> 
    $("#vote_form").slideToggle("slow")
)

$("#vote_rankA").mouseenter( -> $("#vote_rankA").css("background-color", '#E6FFE6').css( 'cursor', 'pointer' ))
$("#vote_rankA").mouseleave( -> $("#vote_rankA").css("background-color", '#F5F5F5'))

$("#vote_rankB").mouseenter( -> $("#vote_rankB").css("background-color", '#FFF4E6').css( 'cursor', 'pointer' ))
$("#vote_rankB").mouseleave( -> $("#vote_rankB").css("background-color", '#F5F5F5'))

$("#vote_rankC").mouseenter( -> $("#vote_rankC").css("background-color", '#E6F7FF').css( 'cursor', 'pointer' ))
$("#vote_rankC").mouseleave( -> $("#vote_rankC").css("background-color", '#F5F5F5'))

$("#vote_rankD").mouseenter( -> $("#vote_rankD").css("background-color", '#FFE6E6').css( 'cursor', 'pointer' ))
$("#vote_rankD").mouseleave( -> $("#vote_rankD").css("background-color", '#F5F5F5'))

$("#vote_rankA").click( ->
    
)

###
$("#btn_show_new_url").click( -> 
    $(".add_url_form").slideToggle("fast")
)


$("#submit_add_url").click( ->
    $.ajax(
        url: '/link'
        type: 'POST'
        data: 
            url: $("#input_form_add_url").val()
            conference_id: $("#conference_id").val()
            label: $("#formAddUrlLabel").val()
        success: (data) ->
            $('#confLinks').append(data)
            $(".add_url_form").slideToggle("fast")
        error: (data) -> 
            message.addError(data.responseText)
        complete: (data, x, e) -> 
    )
)

window.deleteUrl = (id) -> 
    $.ajax({
        type: 'DELETE',
        url: '/link/' + id
    });
    $('#link_' + id).hide("slow")

###