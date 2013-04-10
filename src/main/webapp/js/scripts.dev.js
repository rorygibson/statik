var $ = jQuery.noConflict();
$(document).ready(function(){

	/* TOUCH SAVERS */

	var touchM = "ontouchstart" in window;

	if(touchM){
		$('.socialIcons a').css('opacity', 1);
		$('a').each(function(){
			$(this).css('color', $(this).css('color'));
		})
	}

	/* JQUERY ONE PAGE NAV */

	$('#menu ul').onePageNav({
	 	currentClass: 'selected',
	 	changeHash: true,
	 	scrollSpeed: 750
 	});
 	
 	/* MOBILE NAV */

	$('.rMenu').find('select').bind('change', function(){
		$('html, body').stop().animate({scrollTop: $($(this).find('option:selected').data('href')).offset().top}, 1000);
	});
  	
  	/* SCROLL TO TOP */
  	
    function getTopScroll(){
    	return $('html').scrollTop() > 0 ? $('html').scrollTop() : $('body').scrollTop();
    }
	
	$('.top').click(function(){
	  $("html, body").animate({ scrollTop: 0 }, 600);
	  return false;
	});

});
