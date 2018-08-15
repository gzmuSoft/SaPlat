layui.define(['layer', 'element'], function (exports) {
    var $ = layui.jquery;
    $(".money").focus(function () {
        var _fill_ipt = $(this).parent("div");
        if (_fill_ipt.find('.bigtx').size() === 0) {
            var tar = "<div class='bigtx'><span></span><i></i></div>";
            _fill_ipt.append(tar)
        }
    }).bind('input propertychange', function () {
        //console.log(1)
        var tx;
        if ($(this).val().match(/\./g) != null) {
            integerNum = parseInt($(this).val()).toString().replace(/\d(?=(\d{3})+$)/g, '$&,');
            decimalNum = '.' + $(this).val().replace(/(.*)\.(.*)/g, '$2');
            tx = integerNum + decimalNum + "元"
        } else if($(this).val().length!=0){
            tx = $(this).val().replace(/\d(?=(\d{3})+$)/g, '$&,') + "元";
        }
        else{
            tx="0元";
        }

        var _format_value = tx;
        if (_format_value !== '' && !isNaN(_format_value)) {
            var _value_array = _format_value.split('.');
            var _int = _value_array[0];
            var _decimal = '';
            if (_value_array.length > 1) {
                _decimal = _value_array[1];
            }

            var _int_str = '';
            var _count = 0;

            for (var i = _int.length - 1; i >= 0; i--) {
                _count++;
                _int_str = _int.charAt(i) + _int_str;
                if (!(_count % 3) && i !== 0) {
                    _int_str = ',' + _int_str;
                }
            }

            _format_value = _int_str;

            if (_decimal !== '') {
                _format_value += '.' + _decimal;
            }
        }

        $(this).siblings(".bigtx").css({
            "top": "-37px",
            "opacity": "1"
        });
        $(this).siblings(".bigtx").find("span").html(_format_value);
        if (_format_value === "") {
            $(this).siblings(".bigtx").css({
                "top": "0",
                "opacity": "0"
            })
        }
    }).blur(function () {
        $(this).siblings(".bigtx").css({
            "top": "0",
            "opacity": "0"
        });
        var _fill_ipt = $(this).parent("div");
        if (_fill_ipt.find('.bigtx').size() > 0) {
            setTimeout(function () {
                _fill_ipt.find('.bigtx').remove();
            }, 200);
        }
    });
});