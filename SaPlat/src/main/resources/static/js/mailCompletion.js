/*
 * 邮箱地址补全插件
 * power by hcoder.net
 */
function hcMailCompletion(mailId){
	//常用属性设置
	this.hcMailList = new Array('qq.com','163.com','sina.com','126.com','sohu.com','139.com','gmail.com','hotmail.com','21cn.com');
	this.padding             = 2;
	this.borderWidth         = 1;
	this.borderColor         = '#D1D1D1';
	this.borderType          = 'solid';
	this.bgColor             = '#FFFFFF';
	this.keySelectedBgColor  = '#F2F2F2';
	this.keySelectedTxtColor = '#EF783C';
	this.marginTop           = 1;
	this.obj                 = $('#'+mailId);
	this.mailOptionCss       = {'height':'30px', 'lineHeight':'30px', 'padding':'0px 6px'}
	//运行接口
	this.run                 = function(){
		var sObj             = this;
		this.obj.keyup(function(e){
			//键盘上下选择
			if(e.which == 38 || e.which == 40){
				return sObj.hcMailCompletionKeySelect(e.which);
			}
			//回车
			if(e.which == 13){return sObj.hcMailCompletionEnter();}
			var thisVal = $(this).val();
			if(thisVal.length < 2){return false;}
			//输入到@ 展示邮件列表
			sObj.hcShowMailList();
		});
		
		this.obj.focusout(function(){
			setTimeout(function(){
				$('#hcShowMailList').remove();
				$('#hcMailCompletionCss').remove();
			},200);
		});
	}
	
	//回车处理
	this.hcMailCompletionEnter = function(){
		if($('#hcShowMailList').is(":hidden")){return null;}
		if($('#hcShowMailList').find('div').size() < 1){return null;}
		var sedIndex = $('#hcShowMailList').find('.hcShowMailListSed').index();
		if(sedIndex == -1){
			$('#hcShowMailList').remove();
			$('#hcMailCompletionCss').remove();
			return null;
		}
		this.obj.val($('#hcShowMailList').find('div').eq(sedIndex).text());
		$('#hcShowMailList').remove();
		$('#hcMailCompletionCss').remove();
		return null;
	}
	
	//上下键选择
	this.hcMailCompletionKeySelect = function(key){
		if($('#hcShowMailList').is(":hidden")){return null;}
		if($('#hcShowMailList').find('div').size() < 1){return null;}
		var sedIndex = $('#hcShowMailList').find('.hcShowMailListSed').index();
		//无选中项目
		if(sedIndex == -1){
			$('#hcShowMailList').find('div').eq(0).addClass('hcShowMailListSed');
		}else{
			var totalOption = $('#hcShowMailList').find('div').size();
			//up
			if(key == 38){
				var sIndex = sedIndex - 1;
				if(sIndex < 0){sIndex = sedIndex - 1;}
			}else{
				var sIndex = sedIndex + 1;
				if(sIndex >= totalOption){sIndex = 0;}
			}
			$('#hcShowMailList').find('div').eq(sIndex).addClass('hcShowMailListSed').siblings().removeClass('hcShowMailListSed');
		}
		return null;
	}
	
	//输出备选邮件列表
	this.hcShowMailList = function(){
		var mailVal             = this.obj.val();
		var mailSplit           = mailVal.split('@');
		if(mailSplit.length > 2){return false;}
		//获取输入框的宽度
		var thisEmailWidth      = this.obj.outerWidth();
		//计算邮件列表的宽度
		var hcShowMailListWidth = thisEmailWidth - (this.borderWidth + this.padding) * 2;
		if($('#hcShowMailList').size() < 1){
			var html    = '<style id="hcMailCompletionCss">'+
			'#hcShowMailList{width:'+hcShowMailListWidth+'px; position:absolute; z-index:8; left:0px; top:0px; padding:8px '+this.padding+'px; border:'+this.borderWidth+'px '+this.borderType+' '+this.borderColor+'; overflow:hidden; background:'+this.bgColor+';}'+
			'#hcShowMailList div{height:'+this.mailOptionCss.height+'; line-height:'+this.mailOptionCss.lineHeight+'; overflow:hidden; padding:'+this.mailOptionCss.padding+'; cursor:pointer;}'+
			'.hcShowMailListSed, .hcShowMailListHv{background:'+this.keySelectedBgColor+'; color:'+this.keySelectedTxtColor+';}'+
			'</style>';
			html        += '<div id="hcShowMailList"><div>';
			$(html).appendTo('body');
		}
		
		var sets                = this.obj.offset(); 
		var setLeft             = sets.left;
		var setTop              = sets.top + this.obj.outerHeight() + this.marginTop;
		$('#hcShowMailList').css({left:setLeft, top:setTop});
		$('#hcShowMailList').html('');
		for(k in this.hcMailList){
			$('<div>'+mailSplit[0]+'@'+this.hcMailList[k]+'</div>').appendTo('#hcShowMailList');
		}
		
		$('#hcShowMailList').find('div').hide();
		$('#hcShowMailList').find("div:contains('"+mailVal+"')").show();
		$('#hcShowMailList').find('div:hidden').remove();
		if($('#hcShowMailList').find('div:visible').size() < 1){
			$('#hcShowMailList').remove();
			$('#hcMailCompletionCss').remove();
		}else{
			var sObj             = this;
			//绑定鼠标经过
			$('#hcShowMailList div').hover(function(){$(this).addClass('hcShowMailListHv');},function(){$(this).removeClass('hcShowMailListHv');});
			$('#hcShowMailList div').click(function(){
				sObj.obj.val($(this).text());
			});
		}
	}
}