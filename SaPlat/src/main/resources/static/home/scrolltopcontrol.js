var scrolltotop={
	setting:{
		startline:120,
		scrollto:0, 
		scrollduration:400, 
		fadeduration:[500,100] 
	},
	controlHTML:'<div class="stSharp" style="  background-repeat: no-repeat;background-image:url(http://www.weaver.com.cn/e8/img/topback.png);width:54px;height:54px;cursor:pointer;" title="..."></div>'
		+'<div class="stApply" style="  background-repeat: no-repeat;background-image:url(http://www.weaver.com.cn/e8/img/apply.png);width:54px;height:99px;cursor:pointer;margin-top: -8px;"></div>',
	controlattrs:{offsetx:60,offsety:280},
	anchorkeyword:"#top",
	state:{
		isvisible:false,
		shouldvisible:false
	},scrollup:function(){
		if(!this.cssfixedsupport){
			this.$control.css({opacity:0});
		}
		var dest=isNaN(this.setting.scrollto)?this.setting.scrollto:parseInt(this.setting.scrollto);
		if(typeof dest=="string"&&jQuery("#"+dest).length==1){
			dest=jQuery("#"+dest).offset().top;
		}else{
			dest=0;
		}
		this.$body.animate({scrollTop:dest},this.setting.scrollduration);
	},keepfixed:function(){
		var $window=jQuery(window);
		var controlx=$window.scrollLeft()+$window.width()-this.$control.width()-this.controlattrs.offsetx;
		var controly=$window.scrollTop()+$window.height()-this.$control.height()-this.controlattrs.offsety;
		this.$control.css({left:controlx+"px",top:controly+"px"});
	},togglecontrol:function(){
		var scrolltop=jQuery(window).scrollTop();
		if(!this.cssfixedsupport){
			this.keepfixed();
		}
		this.state.shouldvisible=(scrolltop>=this.setting.startline)?true:false;
		if(this.state.shouldvisible&&!this.state.isvisible){
			this.$control.stop().animate({opacity:1},this.setting.fadeduration[0]);
			this.state.isvisible=true;
			setTimeout(function(){
				jQuery(".applyDiv").css("opacity",1);
			},60);
		}else{
			if(this.state.shouldvisible==false&&this.state.isvisible){
				this.$control.stop().animate({opacity:0},this.setting.fadeduration[1]);
				this.state.isvisible=false;
				setTimeout(function(){
					jQuery(".applyDiv").css("opacity",0);
				},60);
			}
		}
	},init:function(){
		var mainobj=scrolltotop;
		var iebrws=document.all;
		mainobj.cssfixedsupport=!iebrws||iebrws&&document.compatMode=="CSS1Compat"&&window.XMLHttpRequest;
		mainobj.$body=(window.opera)?(document.compatMode=="CSS1Compat"?$("html"):$("body")):$("html,body");
		mainobj.$control = $('<div id="topcontrol">'+mainobj.controlHTML+"</div>")
			.css({position:mainobj.cssfixedsupport?"fixed":"absolute",bottom:mainobj.controlattrs.offsety,right:mainobj.controlattrs.offsetx,opacity:0})
			.appendTo("body");

		mainobj.$control.find('.stSharp').click(function() {
			mainobj.scrollup();
			return false;
		});

		mainobj.$control.find('.stSharp').hover(function(){
			  $(this).css("backgroundImage","url(http://www.weaver.com.cn/e8/img/topback_hover.png)")
		},function(){
			  $(this).css("backgroundImage","url(http://www.weaver.com.cn/e8/img/topback.png)")
		});
        
        

		mainobj.$control.find('.stApply').hover(function(){
			$(this).css("backgroundImage","url(http://www.weaver.com.cn/e8/img/apply_hover.png)")
		},function(){
			$(this).css("backgroundImage","url(http://www.weaver.com.cn/e8/img/apply.png)")
		});
        
		var e_url = window.location.href;
		if(e_url.indexOf("role.html")>0 || e_url.indexOf("wechat.html")>0 || e_url.indexOf("socials.html")>0){
	
		}else{
		   mainobj.$control.find('.stSharp').css("display","none");
		   mainobj.$control.find('.stApply').css("display","none");
		}
		

		if(jQuery.fn.themePopover) mainobj.$control.find('.stApply').themePopover();

		if(document.all&&!window.XMLHttpRequest&&mainobj.$control.text()!=""){
			mainobj.$control.css({width:mainobj.$control.width()});
		}

		mainobj.togglecontrol();

		$('a[href="'+mainobj.anchorkeyword+'"]').click(function(){mainobj.scrollup();return false;});

		$(window).bind("scroll resize",function(e){mainobj.togglecontrol();});
	}
};

jQuery(document).ready(function(){
	scrolltotop.init();

	var top = jQuery("#topcontrol").offset().top;
	var scrollTop = jQuery(window).scrollTop();
	top = top-scrollTop;
	top = top+56;
	jQuery(".applyDiv").css("top",top+"px");
	jQuery(".applyDiv").css("opacity",0);
});