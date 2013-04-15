$(document).ready(function(){

    var items = $('p');
    var path = window.location.pathname;


    $.each(items, function(index, item) {
        $(item).attr('contenteditable','true');


        $(item).focus(function() {
            $(this).data('before', $(this).html());
        });

        $(item).live('blur',function(){
            var $this = $(this);
            if ($this.data('before') !== $this.html()) {
                $this.data('before', $this.html());

                $.ajax({
                    type:'POST',
                    url:'/content',
                    data:{
                        path: path,
                        content: $(this).text(),
                        selector: getPath(this)
                    },
                    success:function(msg){
                        if(!msg){
                            console.error('update failure');
                        }
                    }
                });
            }

        });
    });

    var authenticatedAuthBox = '<div id="ces-auth-box" style="position:absolute; top:20px; right:20px; border: solid lightgrey 1px; background-color: lightgray; border-radius: 4px; padding: 5px"><a style="color: blue" href="/logout">Log out</a></div>';
    $('body').append(authenticatedAuthBox);
});
