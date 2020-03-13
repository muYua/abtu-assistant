require.config({
    baseUrl: 'js',
    paths: {
        utils: 'utils/utils',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            exports: 'layui'
        }
    }
});

require(['layui', 'utils'], function (layui, utils) {
    layui.use(['form', 'layer'], function () {
        let form = layui.form
            , layer = layui.layer
            , $ = layui.$;

        //抖动弹窗
        function layerAnim6(message) {
            layer.msg(message, {
                anim: 6, //抖动
                time: 1000 //1秒关闭（如果不配置，默认是3秒）
            })
        }

        const EMAIL_DOM = $("input[name='email']")
        	, VERIFY_CODE_DOM = $("input[name='verifyCode']")
        	, GET_VERIFY_CODE_BUTTON = $("input[name='getVerifyCodeButton']")
        	, CHECK_VERIFY_CODE_BUTTON = $("input[name='checkVerifyCodeButton']");

        function getActivateResetCode() {
            let email = EMAIL_DOM.val().trim();

            //电子邮箱合法性
            if (utils.isEmpty(email)) {
            	layerAnim6("请填入电子邮箱!");
                EMAIL_DOM.focus();
                return false;
            } else {
                if (!utils.isEmail(email)) {
                    layerAnim6("请填入正确的电子邮箱!");
                    EMAIL_DOM.val("");
                    EMAIL_DOM.focus();
                    return false;
                }
            }

            /*获取验证码倒计时*/
    		let timer //timer变量，控制时间
    			, curCount = 60;//当前剩余秒数，并赋初值
    		GET_VERIFY_CODE_BUTTON.prop("disabled", true); //设置按钮为禁用状态
    		GET_VERIFY_CODE_BUTTON.val(curCount + "秒后重获");
    		// 启动计时器timer处理函数，1秒执行一次
    		timer = window.setInterval(setRemainTime, 1000);
    		//timer处理函数
    		function setRemainTime() {
    			if (curCount === 0) {//超时重新获取验证码
    				window.clearInterval(timer);// 停止计时器
    				GET_VERIFY_CODE_BUTTON.removeProp("disabled");//移除禁用状态改为可用
    				GET_VERIFY_CODE_BUTTON.val("获取验证码");
    			} else {
    				curCount--;
    				GET_VERIFY_CODE_BUTTON.val(curCount + "秒后重获");
    			}
    		}
            
            $.ajax({
                url: utils.getDomainName() + '/getResetPasswordVerifyCode',
                type: 'get',
                dataType: 'json',
                timeout: '10000',
                data: {
                    email: email
                },
                success: function (data) {
                    if (data.success) {
                        layer.msg('邮箱验证码发送成功！', {anim: 0}, function () {
                        });
                    } else {
                        if(utils.isEmpty(data.msg))
                            layer.msg("邮箱验证码发送失败！", {time: 2000, anim: 0});
                        else
                            layer.msg(data.msg, {time: 2000, anim: 0});
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax
        }//end getActivateResetCode()

        //按钮触发事件
        GET_VERIFY_CODE_BUTTON.on('click', function () {
        	getActivateResetCode(); 
        });
        
        function checkActivateResetCode() {
        	let email = EMAIL_DOM.val().trim();
        	let verifyCode = VERIFY_CODE_DOM.val().trim();

        	if (utils.isEmpty(email)) {
				layerAnim6("电子邮箱为空!");
				EMAIL_DOM.focus();
				return false;
			}
        	
			if (utils.isEmpty(verifyCode)) {
				layerAnim6("请填入验证码!");
				VERIFY_CODE_DOM.focus();
				return false;
			}
        	
            $.ajax({
                url: utils.getDomainName() + '/checkResetPasswordVerifyCode',
                type: 'post',
                dataType: 'json',
                timeout: '10000',
                data: {
                    email: email,
                    verifyCode: verifyCode
                },
                success: function (data) {
                    if (data.success) {
                        window.location.href=`forgotPasswordNext.html?email=${email}`;
                    } else {
                        if(utils.isEmpty(data.msg))
                            layer.msg("验证码错误！", {time: 2000, anim: 0});
                        else
                            layer.msg(data.msg, {time: 2000, anim: 0});
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax
        }//end getActivateResetCode()
        
        //按钮触发事件
        CHECK_VERIFY_CODE_BUTTON.on('click', function () {
        	 checkActivateResetCode();
        });
        //回车触发
        VERIFY_CODE_DOM.on('keyup', function () {
            if(utils.isPressEnter())
            	checkActivateResetCode();
        });
    });//end layui

});//end require