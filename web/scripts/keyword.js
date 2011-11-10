function postTheForm(theForm){
    $.post(
        theForm.keyword, 
        $(theForm).serializeArray(), 
        function(data, textStatus, xmlHttpRequest){
            $("#postResults").html(data);
        }
    );
}