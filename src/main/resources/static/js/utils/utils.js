/* 声明一个工具包模块 */
define({

	//规范域名
	getDomainName : function() {
		return "http://127.0.0.1:8080"
	},

	//判断是否按下回车
	isPressEnter : function() {
		return event.keyCode === 13;
	},

	//判断字符串是否为空
	isEmpty : function(obj) {
		//typeof obj == "undefined"，判断obj是否定义
		return typeof obj == "undefined" || obj == null || obj === "";
	},

	//判断日期是否格式化输入
	isFormatDate : function(str) {
		const format = /^([12])9[\d]{2}-(0\d|1[0-2])-([012]\d|3[01])$/;
		return format.test(str);
	},

	//判断手机号合法性
	isPhoneNumber : function(str) {
		const phone = /^1[3-9]\d{9}$/;
		return phone.test(str);
	},

	//判断电子邮件合法性
	isEmail : function(str) {
		const email = /^[a-zA-Z\d](\w|-|\+)+@(qq|163|126|yeah|gmail|outlook|hotmail|live|aliyun)\.(com|net)$/;
		return email.test(str);
	},

	//判断密码合法性
	isPassword : function(str) {
		const pswd = /^[\S]{6,16}$/;
		return pswd.test(str);
	},
	
	//获取当前URL里？携带的参数(不含特殊参数如携带=)
	/*getUrlParam: function (name) {
		// 取得url中?后面的字符
		let query = window.location.search.substring(1);
		// 把参数按&拆分成数组
		let param_arr = query.split("&");
		for (let i = 0; i < param_arr.length; i++) {
			let pair = param_arr[i].split("=");
			if (pair[0] === name) {
				return pair[1];
			}
		}
		return false;
	},*/

	//通过正则表达式获取URL参数
	getUrlParam : function(name) {
		let pattern = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		let result = window.location.search.substr(1).match(pattern);//得到？后面的参数，并匹配正则
		if (result != null)
			return unescape(result[2]);
		return null;
	},

	//替换URL参数中的特殊字符
	replaceUrlParam : function(url) {
		return escape(url).replace(/\+/g, '%2B').replace(/\"/g, '%22').replace(
				/\'/g, '%27').replace(/\//g, '%2F');
	},

	getFormatDate : function (formatString) {
		Date.prototype.format = function(format) {
			/*
             * eg:format="YYYY-MM-dd hh:mm:ss";

             */
			let o = {
				"M+" :this.getMonth() + 1, // month
				"d+" :this.getDate(), // day
				"h+" :this.getHours(), // hour
				"m+" :this.getMinutes(), // minute
				"s+" :this.getSeconds(), // second
				"q+" :Math.floor((this.getMonth() + 3) / 3), // quarter
				"S" :this.getMilliseconds() // millisecond
			}
			if (/(y+)/.test(format)) {
				format = format.replace(RegExp.$1, (this.getFullYear() + "")
					.substr(4 - RegExp.$1.length));
			}
			for ( var k in o) {
				if (new RegExp("(" + k + ")").test(format)) {
					format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
						: ("00" + o[k]).substr(("" + o[k]).length));
				}
			}
			return format;
		}
		// let startTime = new Date().format("yyyy-MM-dd 00:00:00");
		// let endTime = new Date().format("yyyy-MM-dd hh:mm:ss");
		return new Date().format(formatString);
	}
});
