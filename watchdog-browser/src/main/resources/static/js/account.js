$(".mode-toggle").click(function (){
	$(this).parents(".login").hide().siblings().show();	
});

$(".login-tab li").click(function () {
	$(this).addClass("login-on").siblings().removeClass("login-on");
	$(".login-mode").eq($(this).index()).show()
                    .siblings().hide();
});

//~ 登录功能
//===============================================================================

// 表单登录



// 短信验证码登录
/**
 * 获取短信随机码(验证码)
 * @param sender 触发该事件的按钮
 */
function acquiresSmsCode(sender) {
    let $mobile = $('#mobile');
    let phone = $.trim($mobile.val());
    if (phone === "") {
        onError('请填写手机号码！');
        $mobile.focus();
        return;
    }

    let url = "/verification.token?type=sms_code&for-phone=" + phone;
    $.ajax({
        type: 'GET',
        url:url,
        success: function() {
            alert("验证码已发送至您的手机号码，请注意查收");
            limitAcquirement(sender);
        },
        error: function(xhr, textStatus){
            onError(textStatus + "：" + xhr.responseText);
        }
    });
}

function limitAcquirement(sender) {
    let count = 60;
    let countDown = function (sender) {
        if (count === 0) {
            sender.value = "获取验证码";
            sender.removeAttribute("disabled");
            count = 60;
        } else {
            count --;
            sender.setAttribute("disabled", true);
            sender.value = "重新发送(" + count + ")";
            setTimeout(function(){
                countDown(sender);
            }, 1000);
        }
    };
    countDown(sender);
}

// 第三方账号登录





