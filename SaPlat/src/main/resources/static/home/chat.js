$(document).ready(function() {
  var domain = 0;
  if(document.domain != "e8.weaver.com.cn") {
    document.domain="weaver.com.cn";
    domain = 1;
  }

  var visitorid = $.cookie("visitorid");
  
  //2018-03-12,该功能对应的jsp已经没有，无法确认是什么功能，暂时先注释相关代码
  /**
  if(visitorid == undefined || visitorid == "") {
    $.ajax({
      type: 'GET',
      url: 'http://e8.weaver.com.cn/chatroom/generateUUID.jsp',
      dataType:'jsonp',
      jsonp:'jsonpcallback',
      success: function(msg){
        if(msg.result) {
          visitorid = msg.result;
          $.cookie("visitorid", msg.result, { expires: 30, path: '/' });
        }
      }
    });
  }**/

  $("#visitorid").val(visitorid);

  var onlineimgtop = "110px";
  var onlineimgright="24px";
  var pagehref = window.location.href;
  if(pagehref.indexOf("role.html")>0){
     onlineimgtop = "116px";
     onlineimgright="83px";
  }

  if(pagehref.indexOf("integrate.html")>0){
     onlineimgtop = "81px";
     onlineimgright="27px";
  }

  if(pagehref.indexOf("mind.html")>0){
     onlineimgtop = "96px";
     onlineimgright="63px";
  }

  var onlineimg = "<div id='onlinechatimg' style=\"display:none;width: 80px;height: 80px;background: url('img/chat/OnlineCon.png') top;position: fixed;top:"+onlineimgtop+";right: "+onlineimgright+";cursor: pointer;z-index: 1000;overflow:hidden;\">"
      +" <div id='chatCnt' style='float: right;height: 27px;width: 27px;line-height: 27px;background-color: #f64500;border-radius: 13px;text-align: center;color: #ffffff;margin: 10px;display:none;'></div>"
      +"</div>";

  
  
  function  openOnlineDialog(){
  
	   if($("#onlinediv").length == 0) {
		  var onlineHtml = "<div id='onlinediv' style=\"border-radius: 3px;background-repeat: repeat-x;width: 657px;box-shadow: 0 0 10px #ccc;background-image:url('img/chat/chat_bg.png');height:535px;position: fixed;top:50%;left:50%;margin:-248px 0 0 -328px;z-index: 1001;overflow:hidden;display:none;cursor:move;filter:'progid:DXImageTransform.Microsoft.Shadow(color=#E9E9E9, direction=0, strength=6) progid:DXImageTransform.Microsoft.Shadow(color=#E9E9E9, direction=90, strength=6) progid:DXImageTransform.Microsoft.Shadow(color=#E9E9E9, direction=180, strength=6) progid:DXImageTransform.Microsoft.Shadow(color=#E9E9E9, direction=270, strength=6)';\">"
			  +"<div id='onlinetitle' style=\"width:100%;height:40px;overflow:hidden;line-height: 40px;font-size: 15px;font-family:微软雅黑;color: #48CAB3;position: absolute;width: 380px;margin-left: 277px;\">"
			  +"  <span style='margin-left:20px;'></span> "
			  +"  <div id='chatClose' title='关闭' style=\"width: 40px;height: 40px;background: url('img/chat/chat-close.png');float:right;cursor: pointer;position: absolute;right: 0px;top: 0px;background-repeat: no-repeat;\"> </div>"
			  +"</div>"
			  +"<iframe id='msgiframe' name='msgiframe' style=\"width: 100%;height:535px;border:0px;overflow:hidden;\" frameborder='no' border='0' allowtransparency='true' src='http://e8.weaver.com.cn/chatroom/pages/chatpage.jsp?visitorid="+visitorid+"&domain="+domain+"' onload=\"$('#loadingmask').hide()\"></iframe>"
			  +"<div id='loadingmask' style=\"position: absolute;width: 100%;height: 535px;top: 41px;left: 0; background: url('img/chat/loading.gif') no-repeat center center;background-color: #ffffff;\"></div>"
			  +"</div>";

      var $onlinediv = $(onlineHtml);

		  $onlinediv.click(function(e) {
        e.stopPropagation();
      }).find('#chatClose').click(function (e) {
        $('#onlinediv').fadeOut();
		  }).hover(function() {
        $(this).css("background", "url('img/chat/chat-close-hover.png')");
		  }, function() {
        $(this).css("background", "url('img/chat/chat-close.png')");
		  });

      $onlinediv.draggable().appendTo('body');
		}

		$('#onlinediv').fadeIn();

		$("#chatCnt").html("").hide();
		window.clearInterval(t);
		t = undefined;
  
  }

  
  $(onlineimg).click(function (e) {
    openOnlineDialog();
    e.stopPropagation();
  }).appendTo('body');

  $(".onlinechat").click(function (e) {
       window.open("http://www.weaver.com.cn/subpage/contact/");
    });

});

var t;

function showMsg(opt) {
  if(!$("#onlinediv").is(":visible") && opt) {
    if(opt.isjoggle == "1") {
      $('#onlinediv').fadeIn();
      $("#chatCnt").html("").hide();
      window.clearInterval(t);
      t = undefined;
    } else if(opt.msglength){
      var charcnt = parseInt($("#chatCnt").html()) || 0;
      charcnt += parseInt(opt.msglength);
      if(charcnt > 0) $("#chatCnt").html(charcnt).show();
      if(!t) {
        t = window.setInterval("shake('onlinechatimg')", 2000);
      }
    }
  }
}

function shake(o){
    var $panel = $("#"+o);
    var box_right = parseInt($panel.css("right"));
    for(var i=1; 4>=i; i++){
        $panel.animate({right:box_right-(20-5*i)},50).animate({right:box_right+2*(20-5*i)},50);
    }
}