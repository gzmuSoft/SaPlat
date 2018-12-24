//全局公共JavaScript

$(document).ready(function() {
	if(document.documentElement.clientHeight >= document.documentElement.offsetHeight) {
       $("#header").css('padding-right', '17px');
    }

	 $(".qcode").bind('click',function(event){
		 $(".wcode").css("display","block");
        event.stopPropagation();
	    
     });
     
     $(".wcode").mouseleave(function(){
	    $(".wcode").css("display","none");
	 });
     

	 $(document).bind('click',function(ev){
         $(".wcode").css("display","none");
     });
     
   $(".w_wrap2 ").css("position","relative").prepend('<div style="width: 200px; text-align: right; float: right; margin-top: 4px; margin-right: 10px; position: absolute; right:0;"></div>');


});