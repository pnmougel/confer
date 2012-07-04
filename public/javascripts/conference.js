



$("#xx").click(function () {
    $(".add_url_form").toggle("fast");
});

$("#submit_add_url").click(function () {
	$.post('/conference/' + '3' + '/addurl', 
		{'url':encodeURIComponent($("#input_form_add_url").val())},
		// {'url':$("#input_form_add_url").val()},
		function(data) {
			$('#links').append('<p>Test</p>');
		}
	);
	alert("fdsdfqq");
});

