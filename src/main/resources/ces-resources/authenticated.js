$(document).ready(function(){

    var items = $('p');



    $.each(items, function(index, item) {
        $(item).attr('contenteditable','true');

        $(item).live('blur',function(){
        	$.ajax({
        		type:'POST',
        		url:'/content',
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

    var authenticatedAuthBox = '<div id="ces-auth-box" style="position:absolute; top:20px; right:20px; border: solid lightgrey 1px; background-color: lightgray; border-radius: 4px; padding: 5px"><a style="color: blue" href="/logout">Log out</a></div>';
    $('body').append(authenticatedAuthBox);
});
