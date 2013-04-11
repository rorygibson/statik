$(document).ready(function(){

    var items = $('*[data-content-id]');

    $.each(items, function(index, item) {
        var contentId = $(item).attr('data-content-id');

        $.get('/content/' + contentId, function(data) {
            if (data) {
                $(item).html(data);
            }
            $(item).attr('contenteditable','true');
        });

        $(item).live('blur',function(){
        	$.ajax({
        		type:'POST',
        		url:'/content/' + contentId,
        		data:{
        			content: $(this).text(),
                    selector: $(this).getPath()
        		},
        		success:function(msg){
        			if(!msg){
        				console.error('update failure');
        			}
        		}
        	});
        });
    });
});
