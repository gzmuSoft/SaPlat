/////////////////////
//check: 1 domain; 2 gif; 3debuginfo
/////////////////////
var namespace= "sogou.sa.test"
var N = {};//N stand for namespace

N.register = function(fullNS)
{
	var nsArray = fullNS.split('.');
	var sEval = "";
	var sNS = "";
	for (var i = 0; i < nsArray.length; i++)
	{
		if (i != 0) sNS += ".";
		sNS += nsArray[i];
		sEval += "if (typeof(" + sNS + ") == 'undefined') " + sNS + " = new Object();"
	}
	if (sEval != "") eval(sEval);
}

N.register(namespace);
N.host="http://hermes.sogou.com"; 	//host
N.pingback_arr=new Array(10);
N.pb_file="sa.gif";			//gif
N.stay_time=0;
N.load_time=new Date();
N.pvid = N.load_time.getTime()+"_"+Math.floor(Math.random()*100000);
N.sogou_document_refer="";
N.last_pb_time=N.load_time;
N.version=1;	//version
N.d=window;
N.tags={};
N.tags.se=0;
N.tags.sw="";
N.curtime=0;
N.interval=1800000;

N.init=function(){
	if (window.parent != window.self){
		try{
			N.sogou_document_refer = parent.document.referrer;
		}catch(err) {
			N.sogou_document_refer = document.referrer;
		}
	}else{
		N.sogou_document_refer = document.referrer;
	}
		N.sogou_document_refer=(typeof (encodeURIComponent)=="function")?encodeURIComponent(N.sogou_document_refer):N.sogou_document_refer;
	N.getSs();
}

N.pingback=function( url1 ){
	var V=N.pingback_arr.length;
	N.pingback_arr[V]=new Image();
	N.pingback_arr[V].src=url1;									
        return true;
}


N.send_pb=function(name,link){
	var d=new Date();
	var act_time=d-N.last_pb_time;
	N.last_pb_time=d;
	N.pingback( N.host + "/"+N.pb_file+"?p="+N.pvid+"&t=" + d.getTime() +"&n="+name+"&r="+N.sogou_document_refer+"&l="+link+"&ln="+N.getSession()+"&s="+(d-N.load_time)+"&a="+act_time+"&v="+N.version+"&ls="+"&sgsa_id="+N.getCookie("sgsa_id")+"&ua="+N.getUa()+"&se="+N.tags.se+"&sw="+N.tags.sw+"&ds="+N.getDs()+"&cl="+N.getCl()+"&ui="+N.getUserId()+"&pf="+N.getPF()+"&ol="+N.getOnloadtime());
}

N.pb4pv=function(){
	N.send_pb("pb_pv","");
}

N.getDs=function(){
	return (N.d.screen.width+"x"+N.d.screen.height)
}

N.getCl=function(){
	return (N.d.screen?N.d.screen.colorDepth+"-bit":"")
}

N.getUa=function(){
	var ua_list=["MSIE 7.0","MSIE 6.0","Firefox"]
	ua=navigator.userAgent;
	for(var o=0;o<ua_list.length;o++){
		if(ua.indexOf(ua_list[o])>-1){
			//alert(ua+"|"+ua_list[o]);
			return o;
		}
	}
	//return navigator.userAgent;
	return -1;
}
N.getPF=function(){
	return navigator.platform ;
}
N.getValue=function(m,n){
		var l=new RegExp("(^|&|\\?)"+m+"=([^&]*)(&|\x24|#)");
		var k=n.match(l);
		return k?k[2]:""
}

N.getSs=function(){
	var c = {
		sindex : [1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13],
		sengin : ["baidu.com", "baidu.com", "google.com", "google.cn", "sogou.com", "zhongsou.com", "search.yahoo.com","one.cn.yahoo.com", "soso.com", "114search.118114.cn", "search.live.com", "youdao.com", "gougou.com", "bing.com"],
		sword : ["word", "wd", "q", "q", "query", "w", "p", "p", "w", "kw", "q", "q", "search", "q"]
	}	
	k=document.referrer;
	var w=c.sengin;
	var u=c.sword;
	var q=c.sindex;
	for(var o=0;o<w.length;o++){
		if((k.indexOf(w[o])>-1)&& N.getValue(u[o],k).length>0){
			N.tags.sw=N.getValue(u[o],k);
			N.tags.se=q[o];
			return
		}
	}
}

N.setCookie=function(c_name,value,domain,path,exdays)
{
	var exdate=new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value=(value) + ((exdays==null) ? "" : "; expires="+exdate.toGMTString()) + "; domain="+domain + "; path="+path;
	document.cookie=c_name + "=" + c_value;
}

N.getCookie=function(l){
	var m=new RegExp("(^| )"+l+"=([^;]*)(;|\x24)");
	var k=m.exec(document.cookie);
	if(k){return k[2]||""}return""	
}

N.getSUV=function(){
	var c=Math.round((new Date().getTime()+Math.random())*1000);
	if(document.cookie.indexOf("sgsa_id")<0){
		N.setCookie("sgsa_id",N.getDomain()+"|"+c,N.getDomain(),"/",365)
	}else{
		//alert("here|"+document.cookie);
		//N.getCookie("sgsa_id"));
	}
};
N.getUserId=function(){
	try{
		return  _sogou_sa_q[0][1];
	}catch(e){
		return "unknown"
	}
}
N.getOnloadtime=function(){
	try {
		return (new Date()).getTime()-_sgsa_onloadtime;
	}catch(e){
		return -1;
	}
}

N.getDomain=function(){
	var  arydomain = new Array(".com.cn",".net",".net.cn",".cc",".org",".org.cn",".gov.cn",".info",".biz",".tv",".cn",".com"); 
	var  domain  =  document.domain;  
	var  tmpdomain  =  "";  
	for(var  i=0;i<arydomain.length;  i++)  
	{  
        	tmpdomain  =  arydomain[i];  
		var k=domain.lastIndexOf(tmpdomain);
         	if(k  !=  -1 && k+tmpdomain.length==domain.length)  
		{  
			domain  =  domain.substr(0,k);
			domain  =  domain.substring(domain.lastIndexOf(".")+1,domain.length);  
			domain  =  domain  +  tmpdomain;  
			break;
		}  
	}  
	return domain
}

N.getSession=function(){
	var d = N.load_time.getTime();
	N.curtime = N.getCookie("sgsa_vt_"+N.getUserId().replace(/-/g,"_")) || 0;	
	N.setCookie("sgsa_vt_"+N.getUserId().replace(/-/g,"_"), d, N.getDomain(),"/");
	if(d - N.curtime > N.interval){
		return 1;		
	}
	return 2;	
}

N.init();
N.getSUV()
N.pb4pv();

document.write(unescape("%3Cscript src='http://pb.kspost.sogou.com/lyb.js' type='text/javascript'%3E%3C/script%3E"));
