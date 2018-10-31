
function GetCookie(name) { 
	var arg = name + "="; 
	var alen = arg.length; 
	var clen = window.document.cookie.length; 
	var i = 0; 
	while (i < clen) { 
		var j = i + alen; 
		if (window.document.cookie.substring(i, j) == arg) return getCookieVal (j); 
		i = window.document.cookie.indexOf(" ", i) + 1; 
		if (i == 0) break; 
	} 
	return null; 
} 

function getCookieVal (offset) { 
	var endstr = window.document.cookie.indexOf (";", offset); 
	if (endstr == -1) endstr = window.document.cookie.length; 
	return unescape(window.document.cookie.substring(offset, endstr)); 
} 

var __ranknow_tj_loaded;
if(__ranknow_tj_loaded == null)
{
	__ranknow_tj_loaded = (new Date().getTime());
// ERROR003: 您的RankNow防范恶意点击服务已于2017-05-16过期，功能已暂停，请尽快联系我们重新开通！
}