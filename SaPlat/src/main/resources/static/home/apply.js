//ad7¹ã¸æ
//document.write("<script type='text/javascript' src='http://public.dmppro.cn/js/i.js?uid=okh' id='_mutmzc'></script>");
var source="";
var kw="";
$(document).ready(function(){

	source = getQueryString("source");
	kw = getQueryString("kw");
	if((source!=null && source!="") || (kw!=null && kw!="")){
		$("a").each(function(){
			var _href = $(this).attr("href");
			if(_href && _href!=""){
				if(source!=null && source!=""){
					if(_href.indexOf("?")>-1){
						_href += "&source="+source;
					}else{
						_href += "?source="+source;
					}
				}
				if(kw!=null && kw!=""){
					if(_href.indexOf("?")>-1){
						_href += "&kw="+kw;
					}else{
						_href += "?kw="+kw;
					}
				}
				$(this).attr("href",encodeURI(_href));
			}
		});
	}
	
	if(ismobile()){
		var url = window.location+"";
		if(url.indexOf("wechat.html")>-1){
			//url = "http://wx.weaver.com.cn/taste";
			url = "/mobile/ecology/wechat.html";
		}else{
			url = "/mobile/ecology/index.html";
		}
		if(source!=null && source!=""){
			url += "?source="+source;
			if(kw!=null && kw!=""){
				url += "&kw="+kw;
			}
		}else{
			if(kw!=null && kw!=""){
				url += "?kw="+kw;
			}
		}
		window.location = url;
	}
	
	if(source==null || typeof(source)=="undefined") source = "";
	if(kw==null || typeof(kw)=="undefined") kw = "";
	$(document.body).append("<iframe id='paramframe' style='display:none' src='/subpage/apply/applysubmite4session.asp?source="+source+"&kw="+kw+"'></iframe>");
	/**
	$.ajax({
			type: "post",
			url: "/subpage/apply/applysubmite4session.asp",
		  data:{"source":source,"kw":encodeURIComponent(kw)}, 
		  contentType : "application/x-www-form-urlencoded;charset=UTF-8",
		  success: function(data){
		  	alert(data);
			}  	
	});*/
});

function ismobile() {
    try {
        if ((navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i))) {
            return true;
        }
    } catch(d) {}
    	
   	return false;
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r;
    try{
    	r = decodeURI(window.location.search).substr(1).match(reg);
	  }catch(e){
	  	r = window.location.search.substr(1).match(reg);
	  }
    if (r != null) return unescape(r[2]); return null;
}

